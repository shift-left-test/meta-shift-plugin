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
import com.lge.plugins.metashift.metrics.Queryable;
import com.lge.plugins.metashift.models.Recipes;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
public class MetaShiftBuildAction extends MetaShiftActionBaseWithMetrics
    implements RunAction2, Queryable<List<MetaShiftRecipeAction>> {

  private transient Run<?, ?> run;
  private transient List<MetaShiftRecipeAction> recipeActions;

  private final Criteria criteria;

  /**
   * Default constructor.
   */
  public MetaShiftBuildAction(Run<?, ?> run, Criteria criteria, Recipes recipes) {
    super(criteria);

    this.run = run;
    this.criteria = criteria;

    this.getMetrics().parse(recipes);

    recipes.forEach(recipe -> addAction(new MetaShiftRecipeAction(this, criteria, recipe)));
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
   * return all recipes list.
   *
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONObject getRecipesTableModel(int pageIndex, int pageSize) {
    JSONObject result = new JSONObject();
    JSONArray data = new JSONArray();

    int recipeCount = this.getRecipes().size();
    int baseIndex = pageSize * (pageIndex - 1);

    for (int i = 0; i < pageSize; i++) {
      int index = baseIndex + i;
      if (index >= recipeCount) {
        break;
      }

      MetaShiftRecipeAction recipe = this.getRecipes().get(index);
      JSONObject recipeObj = JSONObject.fromObject(recipe.getMetrics());
      recipeObj.element("name", recipe.getDisplayName());
      data.add(recipeObj);
    }

    result.put("data", data);
    result.put("last_page", (recipeCount + pageSize - 1) / pageSize);

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

  private Collection<MetaShiftRecipeAction> filteredBy(
      final Predicate<? super MetaShiftRecipeAction> predicate) {
    return this.getRecipes().stream().filter(predicate).collect(Collectors.toList());
  }

  // Queryable interface
  @Override
  public List<MetaShiftRecipeAction> getCacheAvailability() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getCacheAvailability().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getCodeViolations() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getCodeViolations().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getComments() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getComments().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getComplexity() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getComplexity().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getCoverage() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getCoverage().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getDuplications() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getDuplications().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getMutationTest() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getMutationTest().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getRecipeViolations() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getRecipeViolations().isQualified()));
  }

  @Override
  public ArrayList<MetaShiftRecipeAction> getTest() {
    return new ArrayList<>(filteredBy(o ->
        o.getMetrics().getTest().isQualified()));
  }
}
