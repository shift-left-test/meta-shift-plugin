/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.metrics.CodeSizeDelta;
import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.metrics.QualifiedRecipeCounter;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.RecipesTreemapModel;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import hudson.FilePath;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Actionable;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import jenkins.model.Jenkins;
import jenkins.model.RunAction2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * The main post build action class.
 */
@ExportedBean
public class MetaShiftBuildAction extends Actionable implements RunAction2 {

  static final String STORE_KEY_RECIPEMETRICSLIST = "RecipeMetricsList";

  private transient Run<?, ?> run;
  private transient List<RecipeAction> recipeActions;

  private final Criteria criteria;
  private final QualifiedRecipeCounter qualifiedRecipeCounter;
  private Metrics metrics;

  private final DataSource dataSource;

  /**
   * Default constructor.
   */
  public MetaShiftBuildAction(Run<?, ?> run, TaskListener listener,
      Criteria criteria, FilePath reportRoot, DataSource dataSource)
      throws IOException, InterruptedException {
    super();

    this.run = run;
    this.criteria = criteria;
    this.dataSource = dataSource;
    this.metrics = new Metrics(criteria);

    Recipes recipes = new Recipes(new File(reportRoot.toURI()), listener.getLogger());

    listener.getLogger().println("Parse project recipes metrics");
    this.metrics = new Metrics(criteria);
    this.metrics.parse(recipes);

    listener.getLogger().println("Parse qualifed recipe counter");
    this.qualifiedRecipeCounter = new QualifiedRecipeCounter(criteria);
    this.qualifiedRecipeCounter.parse(recipes);

    JSONArray recipeMetricsArray = new JSONArray();

    for (Recipe recipe : recipes) {
      listener.getLogger().printf("Create recipe[%s] report%n", recipe.getRecipe());
      RecipeAction recipeAction = new RecipeAction(
          this, listener, criteria, reportRoot, dataSource, recipe);
      this.addAction(recipeAction);
      long codeLines = recipeAction.getMetrics().getCodeSize() != null
          ? recipeAction.getMetrics().getCodeSize().getLines() : 0;
      JSONObject recipeMetrics = new JSONObject();
      recipeMetrics.put("name", recipeAction.getName());
      recipeMetrics.put("lines", codeLines);

      recipeMetrics.put("premirrorCache", recipeAction.getMetrics().getPremirrorCache());
      recipeMetrics.put("sharedStateCache", recipeAction.getMetrics().getSharedStateCache());
      recipeMetrics.put("recipeViolations", recipeAction.getMetrics().getRecipeViolations());
      recipeMetrics.put("premirrorCache", recipeAction.getMetrics().getPremirrorCache());
      recipeMetrics.put("comments", recipeAction.getMetrics().getComments());
      recipeMetrics.put("codeViolations", recipeAction.getMetrics().getCodeViolations());
      recipeMetrics.put("complexity", recipeAction.getMetrics().getComplexity());
      recipeMetrics.put("duplications", recipeAction.getMetrics().getDuplications());
      recipeMetrics.put("test", recipeAction.getMetrics().getTest());
      recipeMetrics.put("statementCoverage", recipeAction.getMetrics().getStatementCoverage());
      recipeMetrics.put("branchCoverage", recipeAction.getMetrics().getBranchCoverage());
      recipeMetrics.put("mutationTest", recipeAction.getMetrics().getMutationTest());
      
      recipeMetricsArray.add(recipeMetrics);
    }

    try {
      dataSource.put(recipeMetricsArray, STORE_KEY_RECIPEMETRICSLIST);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public String getIconFileName() {
    Jenkins jenkins = Jenkins.getInstanceOrNull();
    if (jenkins == null) {
      return "";
    }

    PluginWrapper wrapper = jenkins.getPluginManager().getPlugin(MetaShiftPlugin.class);

    if (wrapper == null) {
      return "";
    }

    return "/plugin/" + wrapper.getShortName() + "/img/meta_shift_first.png";
  }

  @Override
  public String getDisplayName() {
    return "Meta Shift Report";
  }

  @Override
  public String getUrlName() {
    return "metashift_build";
  }

  // need to render at side panel link
  public String getUrl() {
    return getRun().getUrl() + getUrlName() + "/";
  }

  // implements RunAction2 API
  @Override
  public void onAttached(Run<?, ?> run) {
    this.run = run;
  }

  @Override
  public void onLoad(Run<?, ?> run) {
    this.run = run;
  }

  @Override
  public String getSearchUrl() {
    return getUrlName();
  }

  public Criteria getCriteria() {
    return this.criteria;
  }

  /**
   * Returns the run object which generated this action.
   *
   * @return Run class
   */
  public Run<?, ?> getRun() {
    return this.run;
  }

  public Metrics getMetrics() {
    return this.metrics;
  }

  public QualifiedRecipeCounter getQualifiedRecipeCounter() {
    return this.qualifiedRecipeCounter;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * Returns recipeAction list.
   *
   * @return MetaShiftRecipeAction List
   */
  public List<RecipeAction> getRecipes() {
    if (this.recipeActions == null) {
      this.recipeActions = this.getActions(RecipeAction.class);
    }

    return this.recipeActions;
  }

  /**
   * return recipe treemap chart model.
   */
  @JavaScriptMethod
  public JSONObject getRecipesTreemapModel() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    for (RecipeAction recipeAction : this.getRecipes()) {
      model.add(recipeAction.getName(), "",
          (int) recipeAction.getMetrics().getCodeSize().getLines(),
          (int) (recipeAction.getMetrics().getRatio() * 100));
    }
    return JSONObject.fromObject(model);
  }

  /**
   * return paginated recipes list.
   *
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONArray getRecipesTableModel() {
    JSONArray recipeMetricsArray = this.getDataSource().get(
        STORE_KEY_RECIPEMETRICSLIST);

    return recipeMetricsArray;
  }

  /**
   * return recipe count which has available test.
   *
   * @return tested recipe count
   */
  public long getTestedRecipes() {
    return qualifiedRecipeCounter.getTestedRecipes().getNumerator();
  }

  public JSONObject getCodeSizeJson() {
    return JSONObject.fromObject(this.metrics.getCodeSize());
  }

  public JSONObject getPremirrorCacheJson() {
    return JSONObject.fromObject(this.metrics.getPremirrorCache());
  }

  public JSONObject getSharedStateCacheJson() {
    return JSONObject.fromObject(this.metrics.getSharedStateCache());
  }

  public JSONObject getCodeViolationsJson() {
    return JSONObject.fromObject(this.metrics.getCodeViolations());
  }

  public JSONObject getCommentsJson() {
    return JSONObject.fromObject(this.metrics.getComments());
  }

  public JSONObject getComplexityJson() {
    return JSONObject.fromObject(this.metrics.getComplexity());
  }

  public JSONObject getStatementCoverageJson() {
    return JSONObject.fromObject(this.metrics.getStatementCoverage());
  }

  public JSONObject getBranchCoverageJson() {
    return JSONObject.fromObject(this.metrics.getBranchCoverage());
  }

  public JSONObject getDuplicationsJson() {
    return JSONObject.fromObject(this.metrics.getDuplications());
  }

  public JSONObject getMutationTestJson() {
    return JSONObject.fromObject(this.metrics.getMutationTest());
  }

  public JSONObject getRecipeViolationsJson() {
    return JSONObject.fromObject(this.metrics.getRecipeViolations());
  }

  public JSONObject getTestJson() {
    return JSONObject.fromObject(this.metrics.getTest());
  }

  private transient MetaShiftBuildAction previousAction;

  /**
   * return previous not failed build action.
   *
   * @return metashift build action
   */
  public MetaShiftBuildAction getPreviousBuildAction() {
    if (previousAction != null) {
      return previousAction;
    }

    AbstractBuild<?, ?> build = ((AbstractBuild<?, ?>) this.run)
        .getPreviousNotFailedBuild();
    while (build != null) {
      if (build.getResult() != Result.FAILURE) {
        previousAction = build.getAction(MetaShiftBuildAction.class);
        if (previousAction != null) {
          return previousAction;
        }
      }
      build = build.getPreviousNotFailedBuild();
    }
    return null;
  }

  private Metrics getPreviousMetrics() {
    if (getPreviousBuildAction() != null) {
      return getPreviousBuildAction().getMetrics();
    } else {
      return null;
    }
  }

  /**
   * return tested recipe rate change.
   */
  public double getTestedRecipesDelta() {
    MetaShiftBuildAction previous = getPreviousBuildAction();
    double previousRatio = (previous == null) ? 0 :
        previous.getQualifiedRecipeCounter().getTestedRecipes().getRatio();
    return getQualifiedRecipeCounter().getTestedRecipes().getRatio() - previousRatio;
  }

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public JSONObject getCodeSizeDeltaJson() {
    CodeSizeEvaluator previous = Optional.ofNullable(getPreviousMetrics())
        .map(Metrics::getCodeSize).orElse(null);
    CodeSizeEvaluator current = getMetrics().getCodeSize();
    return JSONObject.fromObject(CodeSizeDelta.between(previous, current));
  }

  private double getRatioDelta(Function<Metrics, Evaluator<?>> mapper) {
    Evaluator<?> previous = Optional.ofNullable(getPreviousMetrics()).map(mapper).orElse(null);
    Evaluator<?> current = mapper.apply(getMetrics());
    if (current == null) {
      return 0;
    }
    return (previous != null) ? current.getRatio() - previous.getRatio() : current.getRatio();
  }

  // ratio delta
  public double getPremirrorCacheDelta() {
    return getRatioDelta(Metrics::getPremirrorCache);
  }

  public double getSharedStateCacheDelta() {
    return getRatioDelta(Metrics::getSharedStateCache);
  }

  public double getCodeViolationsDelta() {
    return getRatioDelta(Metrics::getCodeViolations);
  }

  public double getCommentsDelta() {
    return getRatioDelta(Metrics::getComments);
  }

  public double getComplexityDelta() {
    return getRatioDelta(Metrics::getComplexity);
  }

  public double getStatementCoverageDelta() {
    return getRatioDelta(Metrics::getStatementCoverage);
  }

  public double getBranchCoverageDelta() {
    return getRatioDelta(Metrics::getBranchCoverage);
  }

  public double getDuplicationsDelta() {
    return getRatioDelta(Metrics::getDuplications);
  }

  public double getMutationTestDelta() {
    return getRatioDelta(Metrics::getMutationTest);
  }

  public double getRecipeViolationsDelta() {
    return getRatioDelta(Metrics::getRecipeViolations);
  }

  public double getTestDelta() {
    return getRatioDelta(Metrics::getTest);
  }
}
