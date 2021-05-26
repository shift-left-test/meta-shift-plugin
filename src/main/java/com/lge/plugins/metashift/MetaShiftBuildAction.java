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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.metrics.CodeSizeDelta;
import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.ListUtils;
import hudson.FilePath;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Actionable;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
 * MetaShift post build action class.
 */
@ExportedBean
public class MetaShiftBuildAction extends Actionable
    implements RunAction2 {

  private transient Run<?, ?> run;
  private transient List<MetaShiftRecipeAction> recipeActions;

  private final Criteria criteria;
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

    this.metrics = new Metrics(criteria);
    this.metrics.parse(recipes);

    for (Recipe recipe : recipes) {
      MetaShiftRecipeAction recipeAction = new MetaShiftRecipeAction(
          this, listener, criteria, reportRoot, dataSource, recipe);
      this.addAction(recipeAction);
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

  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * Returns recipeAction list.
   *
   * @return MetaShiftRecipeAction List
   */
  public List<MetaShiftRecipeAction> getRecipes() {
    if (this.recipeActions == null) {
      this.recipeActions = this.getActions(MetaShiftRecipeAction.class);
    }

    return this.recipeActions;
  }

  /**
   * return paginated recipes list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONObject getRecipesTableModel(int pageIndex, int pageSize) {
    JSONObject result = new JSONObject();
    JSONArray data = new JSONArray();

    List<List<MetaShiftRecipeAction>> pagedRecipeAction
        = ListUtils.partition(this.getRecipes(), pageSize);

    if (pageIndex < 1) {
      pageIndex = 1;
    } else if (pageIndex > pagedRecipeAction.size()) {
      pageIndex = pagedRecipeAction.size();
    }

    for (MetaShiftRecipeAction recipe : pagedRecipeAction.get(pageIndex - 1)) {
      JSONObject recipeObj = JSONObject.fromObject(recipe.getMetrics());
      recipeObj.element("name", recipe.getDisplayName());
      data.add(recipeObj);
    }

    result.put("data", data);
    result.put("last_page", pagedRecipeAction.size());

    return result;
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
   * return recipe count which has available test.
   *
   * @return tested recipe count
   */
  public long getTestedRecipes() {
    return this.getRecipes().stream().filter(
        o -> o.getMetrics().getTest().isAvailable()).count();
  }

  /**
   * return tested recipe rate change.
   */
  public double getTestedRecipesDelta() {
    MetaShiftBuildAction previous =
        Optional.ofNullable(getPreviousBuildAction()).orElse(null);

    double previousRatio = previous != null ? (double) previous.getTestedRecipes()
        / (double) previous.getMetrics().getCodeSize().getRecipes() : 0;

    return (double) getTestedRecipes() / (double) getMetrics().getCodeSize().getRecipes()
        - previousRatio;
  }

  // metrics delta

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public CodeSizeDelta getCodeSizeDelta() {
    CodeSizeEvaluator previous =
        Optional.ofNullable(getPreviousMetrics())
            .map(Metrics::getCodeSize).orElse(null);
    CodeSizeEvaluator current = getMetrics().getCodeSize();
    return CodeSizeDelta.between(previous, current);
  }

  private double getRatioDelta(Function<Metrics, Evaluator<?>> mapper) {
    Evaluator<?> previous =
        Optional.ofNullable(getPreviousMetrics())
            .map(mapper).orElse(null);
    Evaluator<?> current = mapper.apply(getMetrics());

    if (current == null) {
      return 0;
    }

    return (previous != null) ? current.getRatio() - previous.getRatio()
        : current.getRatio();
  }

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

  public double getCoverageDelta() {
    return getRatioDelta(Metrics::getCoverage);
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

  // qualified rate

  /**
   * return premirror cache qualified recipe rate.
   */
  public double getPremirrorCacheQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getPremirrorCache() != null
        && o.getMetrics().getPremirrorCache().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return sharedstate cache availability qualified recipe rate.
   */
  public double getSharedStateCacheQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getSharedStateCache() != null
        && o.getMetrics().getSharedStateCache().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return code violation qualified recipe rate.
   */
  public double getCodeViolationsQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getCodeViolations() != null
        && o.getMetrics().getCodeViolations().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return comments qualified recipe rate.
   */
  public double getCommentsQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getComments() != null
        && o.getMetrics().getComments().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return complexity qualified recipe rate.
   */
  public double getComplexityQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getComplexity() != null
        && o.getMetrics().getComplexity().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return coverage qualified recipe rate.
   */
  public double getCoverageQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getCoverage() != null
        && o.getMetrics().getCoverage().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return duplications qualified recipe rate.
   */
  public double getDuplicationsQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getDuplications() != null
        && o.getMetrics().getDuplications().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return mutation test qualified recipe rate.
   */
  public double getMutationTestQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getMutationTest() != null
        && o.getMetrics().getMutationTest().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return recipe violation qualified recipe rate.
   */
  public double getRecipeViolationsQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getRecipeViolations() != null
        && o.getMetrics().getRecipeViolations().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return test qualified recipe rate.
   */
  public double getTestQualifiedRate() {
    return (double) this.getRecipes().stream().filter(
        o -> o.getMetrics().getTest() != null
        && o.getMetrics().getTest().isQualified()).count()
        / (double) this.getRecipes().size();
  }

  /**
   * return rank of LineOfCode.
   */
  public long getRecipeLineOfCodeRank(MetaShiftRecipeAction action) {
    List<MetaShiftRecipeAction> recipeActionList =
        new ArrayList<MetaShiftRecipeAction>(this.getRecipes());
    recipeActionList.sort(new Comparator<MetaShiftRecipeAction>() {
      public int compare(MetaShiftRecipeAction a, MetaShiftRecipeAction b) {
        return (int) (b.getLineOfCodeValue() - a.getLineOfCodeValue());
      }
    });

    return recipeActionList.indexOf(action) + 1;
  }

  /**
   * return rank of build performance.
   */
  public long getRecipeBuildPerformanceRank(MetaShiftRecipeAction action) {
    List<MetaShiftRecipeAction> recipeActionList =
        new ArrayList<MetaShiftRecipeAction>(this.getRecipes());
    recipeActionList.sort(new Comparator<MetaShiftRecipeAction>() {
      public int compare(MetaShiftRecipeAction a, MetaShiftRecipeAction b) {
        return (int) (b.getBuildPerformanceValue() - a.getBuildPerformanceValue());
      }
    });

    return recipeActionList.indexOf(action) + 1;
  }

  /**
   * return rank of code quality.
   */
  public long getRecipeCodeQualityRank(MetaShiftRecipeAction action) {
    List<MetaShiftRecipeAction> recipeActionList =
        new ArrayList<MetaShiftRecipeAction>(this.getRecipes());
    recipeActionList.sort(new Comparator<MetaShiftRecipeAction>() {
      public int compare(MetaShiftRecipeAction a, MetaShiftRecipeAction b) {
        return (int) (b.getCodeQualityValue() - a.getCodeQualityValue());
      }
    });

    return recipeActionList.indexOf(action) + 1;
  }
}
