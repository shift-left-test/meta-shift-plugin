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

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.Run;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import jenkins.model.RunAction2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.PropertyFilter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * MetaShift post build action class.
 */
@ExportedBean
public class MetaShiftBuildAction extends MetaShiftActionBaseWithMetrics implements RunAction2 {

  private transient Run<?, ?> run;

  private final Criteria criteria;

  private Integer cachePassedRecipes = 0;
  private Integer codeViolationPassedRecipes = 0;
  private Integer commentPassedRecipes = 0;
  private final Integer complexityPassedRecipes = 0;
  private Integer coveragePassedRecipes = 0;
  private Integer duplicationPassedRecipes = 0;
  private Integer mutationTestPassedRecipes = 0;
  private Integer recipeViolationPassedRecipes = 0;
  private Integer testPassedRecipes = 0;

  /**
   * Default constructor.
   */
  public MetaShiftBuildAction(Run<?, ?> run, Criteria criteria, Recipes recipes) {
    super(criteria);

    this.run = run;
    this.criteria = criteria;

    this.getMetrics().parse(recipes);

    for (Recipe recipe : recipes) {
      MetaShiftRecipeAction recipeAction =
          new MetaShiftRecipeAction(this, this.criteria, recipe);
      this.addAction(recipeAction);

      if (isQualified(recipeAction.getMetrics().getCacheAvailability())) {
        cachePassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getCodeViolations())) {
        codeViolationPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getComments())) {
        commentPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getComplexity())) {
        commentPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getCoverage())) {
        coveragePassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getDuplications())) {
        duplicationPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getMutationTest())) {
        mutationTestPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getRecipeViolations())) {
        recipeViolationPassedRecipes++;
      }
      if (isQualified(recipeAction.getMetrics().getTest())) {
        testPassedRecipes++;
      }
    }
  }

  private boolean isQualified(Evaluator<?> evaluator) {
    return evaluator != null && evaluator.isQualified();
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
    return run;
  }

  public List<MetaShiftRecipeAction> getRecipes() {
    return this.getActions(MetaShiftRecipeAction.class);
  }

  public int getCachePassedRecipes() {
    return this.cachePassedRecipes;
  }

  public int getCodeViolationPassedRecipes() {
    return this.codeViolationPassedRecipes;
  }

  public int getCommentPassedRecipes() {
    return this.commentPassedRecipes;
  }

  public int getComplexityPassedRecipes() {
    return this.complexityPassedRecipes;
  }

  public int getCoveragePassedRecipes() {
    return this.coveragePassedRecipes;
  }

  public int getDuplicationPassedRecipes() {
    return this.duplicationPassedRecipes;
  }

  public int getMutationTestPassedRecipes() {
    return this.mutationTestPassedRecipes;
  }

  public int getRecipeViolationPassedRecipes() {
    return this.recipeViolationPassedRecipes;
  }

  public int getTestPassedRecipes() {
    return this.testPassedRecipes;
  }

  /**
   * check current url is recipe's action.
   *
   * @return is recipe's url or not
   */
  public boolean isRecipeAction() {
    String url = Stapler.getCurrentRequest().getRequestURL().toString();
    String href = getUrl();
    try {
      url = URLDecoder.decode(url, "UTF-8");
      href = URLDecoder.decode(href, "UTF-8");
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      if (href.endsWith("/")) {
        href = href.substring(0, href.length() - 1);
      }

      return url.contains(href);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * filter class for qualifier json serialization.
   */
  public static class CustomPropertyFilter implements PropertyFilter {

    @Override
    public boolean apply(Object source, String name, Object value) {
      return name.equals("collection");
    }
  }

  /**
   * return all recipes list.
   *
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONArray getRecipesTableModel() {
    JSONArray result = new JSONArray();

    for (MetaShiftRecipeAction recipe : this.getRecipes()) {
      JSONObject recipeObj = JSONObject.fromObject(recipe.getMetrics());
      recipeObj.element("name", recipe.getDisplayName());
      result.add(recipeObj);
    }

    return result;
  }

  /**
   * trend chart series data class.
   */
  public static class TrendChartSeries {

    private final String name;
    private final String type;
    private final List<Double> data;

    public String getName() {
      return this.name;
    }

    public String getType() {
      return this.type;
    }

    public List<Double> getData() {
      return this.data;
    }

    /**
     * add data to series with  evaluator.
     *
     * @param evaluator evaluator
     */
    public void addData(Evaluator<?> evaluator) {
      if (evaluator != null && evaluator.isAvailable()) {
        this.data.add(0, evaluator.getRatio() * 100);
      } else {
        this.data.add(0, null);
      }
    }

    /**
     * constructor.
     *
     * @param name series name
     * @param type series type. line, bar, etc...
     */
    public TrendChartSeries(String name, String type) {
      this.name = name;
      this.type = type;
      this.data = new ArrayList<>();
    }
  }

  /**
   * return trend chart data.
   *
   * @return trend chart model
   */
  @JavaScriptMethod
  public JSONObject getTrendChartModel() {
    TrendChartSeries seriesCache = new TrendChartSeries("Cache", "line");
    TrendChartSeries seriesRecipeViolation = new TrendChartSeries("RecipeViolation", "line");
    TrendChartSeries seriesComment = new TrendChartSeries("Comment", "line");
    TrendChartSeries seriesCodeViolation = new TrendChartSeries("CodeViolation", "line");
    TrendChartSeries seriesComplexity = new TrendChartSeries("Complexity", "line");
    TrendChartSeries seriesDuplication = new TrendChartSeries("Duplication", "line");
    TrendChartSeries seriesTest = new TrendChartSeries("Test", "line");
    TrendChartSeries seriesCoverage = new TrendChartSeries("Coverage", "line");
    TrendChartSeries seriesMutation = new TrendChartSeries("Mutation", "line");

    List<String> buildNameList = new ArrayList<>();

    for (AbstractBuild<?, ?> b = (AbstractBuild<?, ?>) this.run;
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }

      MetaShiftBuildAction msAction = b.getAction(MetaShiftBuildAction.class);
      if (msAction == null) {
        continue;
      }
      buildNameList.add(0, b.getDisplayName());

      seriesCache.addData(msAction.getMetrics().getCacheAvailability());
      seriesRecipeViolation.addData(msAction.getMetrics().getRecipeViolations());
      seriesComment.addData(msAction.getMetrics().getComments());
      seriesCodeViolation.addData(msAction.getMetrics().getCodeViolations());
      seriesComplexity.addData(msAction.getMetrics().getComplexity());
      seriesDuplication.addData(msAction.getMetrics().getDuplications());
      seriesTest.addData(msAction.getMetrics().getTest());
      seriesCoverage.addData(msAction.getMetrics().getCoverage());
      seriesMutation.addData(msAction.getMetrics().getMutationTest());

      // TODO: check response series size
      if (buildNameList.size() >= 10) {
        break;
      }
    }

    JSONObject model = new JSONObject();
    model.put("legend", new String[]{
        seriesCache.getName(), seriesRecipeViolation.getName(), seriesComment.getName(),
        seriesCodeViolation.getName(), seriesComplexity.getName(), seriesDuplication.getName(),
        seriesTest.getName(), seriesCoverage.getName(), seriesMutation.getName()
    });
    model.put("builds", buildNameList);
    model.put("series", new TrendChartSeries[]{
        seriesCache, seriesRecipeViolation, seriesComment, seriesCodeViolation,
        seriesComplexity, seriesDuplication, seriesTest, seriesCoverage, seriesMutation
    });

    return model;
  }

  private transient MetaShiftBuildAction previousAction;

  private MetaShiftBuildAction getPreviousBuildAction() {
    if (previousAction != null) {
      return previousAction;
    }

    AbstractBuild<?, ?> build = ((AbstractBuild<?, ?>) this.run)
        .getPreviousSuccessfulBuild();
    while (build != null) {
      if (build.getResult() != Result.FAILURE) {
        previousAction = build.getAction(MetaShiftBuildAction.class);
        if (previousAction != null) {
          return previousAction;
        }
      }
      build = build.getPreviousSuccessfulBuild();
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
   * recipe count diff with previous build.
   *
   * @return recipes diff
   */
  public long getRecipesDiff() {
    long current = this.getMetrics().getCodeSize() != null
        ? this.getMetrics().getCodeSize().getRecipes() : 0;
    long previous = this.getPreviousMetrics() != null
        && this.getPreviousMetrics().getCodeSize() != null
        ? this.getPreviousMetrics().getCodeSize().getRecipes() : 0;

    return current - previous;
  }

  /**
   * line count diff with previous build.
   *
   * @return lines diff
   */
  public long getLinesDiff() {
    long current = this.getMetrics().getCodeSize() != null
        ? this.getMetrics().getCodeSize().getLines() : 0;
    long previous = this.getPreviousMetrics() != null
        && this.getPreviousMetrics().getCodeSize() != null
        ? this.getPreviousMetrics().getCodeSize().getLines() : 0;

    return current - previous;
  }

  /**
   * function count diff with previous build.
   *
   * @return functions diff
   */
  public long getFunctionsDiff() {
    long current = this.getMetrics().getCodeSize() != null
        ? this.getMetrics().getCodeSize().getFunctions() : 0;
    long previous = this.getPreviousMetrics() != null
        && this.getPreviousMetrics().getCodeSize() != null
        ? this.getPreviousMetrics().getCodeSize().getFunctions() : 0;

    return current - previous;
  }

  /**
   * class count diff with previous build.
   *
   * @return classes diff
   */
  public long getClassesDiff() {
    long current = this.getMetrics().getCodeSize() != null
        ? this.getMetrics().getCodeSize().getClasses() : 0;
    long previous = this.getPreviousMetrics() != null
        && this.getPreviousMetrics().getCodeSize() != null
        ? this.getPreviousMetrics().getCodeSize().getClasses() : 0;

    return current - previous;
  }

  /**
   * file count diff with previous build.
   *
   * @return files diff
   */
  public long getFilesDiff() {
    long current = this.getMetrics().getCodeSize() != null
        ? this.getMetrics().getCodeSize().getFiles() : 0;
    long previous = this.getPreviousMetrics() != null
        && this.getPreviousMetrics().getCodeSize() != null
        ? this.getPreviousMetrics().getCodeSize().getFiles() : 0;

    return current - previous;
  }
}
