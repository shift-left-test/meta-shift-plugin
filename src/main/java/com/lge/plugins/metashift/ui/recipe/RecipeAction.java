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

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.metrics.CodeSizeDelta;
import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.MetricStatistics;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SummaryStatistics;
import com.lge.plugins.metashift.ui.MetricsActionBase;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * The recipe action class.
 */
@ExportedBean
public class RecipeAction extends MetricsActionBase implements Action {

  MetaShiftBuildAction parent;

  @Exported(visibility = 999)
  public String name;

  /**
   * Default constructor.
   */
  public RecipeAction(MetaShiftBuildAction parent, TaskListener listener,
      Configuration configuration, FilePath reportRoot, Recipe recipe)
      throws IOException, InterruptedException {
    super(configuration, recipe);

    this.name = recipe.getName();
    this.parent = parent;

    // parse metadata.json
    JSONObject metadata = JsonUtils.createObject(
        reportRoot.child(this.name).child("metadata.json"));

    this.addAction(new RecipeSharedStateCacheAction(
        this, reportRoot.getChannel(), metadata, "Shared State Cache", "sharedstate_cache", true,
        listener, recipe));
    this.addAction(new RecipePremirrorCacheAction(
        this, reportRoot.getChannel(), metadata, "Premirror Cache", "premirror_cache", true,
        listener, recipe));
    this.addAction(new RecipeCodeViolationsAction(
        this, reportRoot.getChannel(), metadata, "Code Violations", "code_violations", false,
        listener, recipe));
    this.addAction(new RecipeCommentsAction(
        this, reportRoot.getChannel(), metadata, "Comments", "comments", true,
        listener, recipe));
    this.addAction(new RecipeComplexityAction(
        this, reportRoot.getChannel(), metadata, "Complexity", "complexity", true,
        listener, recipe));
    this.addAction(new RecipeStatementCoverageAction(
        this, reportRoot.getChannel(), metadata, "Statement Coverage", "statement_coverage", true,
        listener, recipe));
    this.addAction(new RecipeBranchCoverageAction(
        this, reportRoot.getChannel(), metadata, "Branch Coverage", "branch_coverage", true,
        listener, recipe));
    this.addAction(new RecipeDuplicationsAction(
        this, reportRoot.getChannel(), metadata, "Duplications", "duplications", true,
        listener, recipe));
    this.addAction(new RecipeMutationTestAction(
        this, reportRoot.getChannel(), metadata, "Mutation Tests", "mutation_test", true,
        listener, recipe));
    this.addAction(new RecipeRecipeViolationsAction(
        this, reportRoot.getChannel(), metadata, "Recipe Violations", "recipe_violations", false,
        listener, recipe));
    this.addAction(new RecipeTestAction(
        this, reportRoot.getChannel(), metadata, "Unit Tests", "test", true,
        listener, recipe));
  }

  public MetaShiftBuildAction getParentAction() {
    return this.parent;
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public MetricStatistics getMetricStatistics() {
    return this.parent.getMetricStatistics();
  }

  private JSONObject getMetricStatisticsJson(SummaryStatistics statistics,
      Evaluator<?> evaluator, boolean isPercent) {
    JSONObject result = statistics.toJsonObject();
    result.put("scale", evaluator.getRatio());
    result.put("available", evaluator.isAvailable());
    result.put("percent", isPercent);

    return result;
  }

  /**
   * premirrorcache statistics json.
   *
   * @return json string
   */
  public JSONObject getPremirrorCacheStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getPremirrorCache(),
        this.getMetrics().getPremirrorCache(),
        true);
  }

  /**
   * sharedstatecache statistics json.
   *
   * @return json string
   */
  public JSONObject getSharedStateCacheStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getSharedStateCache(),
        this.getMetrics().getSharedStateCache(),
        true);
  }

  /**
   * recipeviolation statistics json.
   *
   * @return json string
   */
  public JSONObject getRecipeViolationsStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getRecipeViolations(),
        this.getMetrics().getRecipeViolations(),
        false);
  }

  /**
   * comments statistics json.
   *
   * @return json string
   */
  public JSONObject getCommentsStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getComments(),
        this.getMetrics().getComments(),
        true);
  }

  /**
   * codeviolations statistics json.
   *
   * @return json string
   */
  public JSONObject getCodeViolationsStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getCodeViolations(),
        this.getMetrics().getCodeViolations(),
        false);
  }

  /**
   * complexity statistics json.
   *
   * @return json string
   */
  public JSONObject getComplexityStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getComplexity(),
        this.getMetrics().getComplexity(),
        true);
  }

  /**
   * duplications statistics json.
   *
   * @return json string
   */
  public JSONObject getDuplicationsStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getDuplications(),
        this.getMetrics().getDuplications(),
        true);
  }

  /**
   * test statistics json.
   *
   * @return json string
   */
  public JSONObject getTestStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getTest(),
        this.getMetrics().getTest(),
        true);
  }

  /**
   * statementcoverage statistics json.
   *
   * @return json string
   */
  public JSONObject getStatementCoverageStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getStatementCoverage(),
        this.getMetrics().getStatementCoverage(),
        true);
  }

  /**
   * branchcoverage statistics json.
   *
   * @return json string
   */
  public JSONObject getBranchCoverageStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getBranchCoverage(),
        this.getMetrics().getBranchCoverage(),
        true);
  }

  /**
   * mutationtest statistics json.
   *
   * @return json string
   */
  public JSONObject getMutationTestStatisticsJson() {
    return this.getMetricStatisticsJson(
        this.parent.getMetricStatistics().getMutationTest(),
        this.getMetrics().getMutationTest(),
        true);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return this.name;
  }

  @Override
  public String getUrlName() {
    return this.name;
  }

  @Override
  public String getSearchUrl() {
    return getUrlName();
  }

  private void addActionToMenu(ContextMenu menu, RecipeActionChild action) {
    if (action != null) {
      String base = Functions.getIconFilePath(action);
      String icon = Stapler.getCurrentRequest().getContextPath()
          + (base.startsWith("images/") ? Functions.getResourcePath() : "")
          + '/' + base;

      menu.add(action.getUrlName(), icon, action.getDisplayName());
    }
  }

  /**
   * constext menu provider for recipe action.
   */
  public ContextMenu doContextMenu(StaplerRequest request, StaplerResponse response)
      throws Exception {
    ContextMenu menu = new ContextMenu();

    final MenuItem headerBuildPerformance = new MenuItem().withDisplayName("Build Performance");
    headerBuildPerformance.header = true;
    menu.add(headerBuildPerformance);
    this.addActionToMenu(menu, this.getAction(RecipePremirrorCacheAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeSharedStateCacheAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeRecipeViolationsAction.class));
    final MenuItem headerCodeQuality = new MenuItem().withDisplayName("Code Quality");
    headerCodeQuality.header = true;
    menu.add(headerCodeQuality);
    this.addActionToMenu(menu, this.getAction(RecipeCommentsAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeCodeViolationsAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeComplexityAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeDuplicationsAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeTestAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeStatementCoverageAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeBranchCoverageAction.class));
    this.addActionToMenu(menu, this.getAction(RecipeMutationTestAction.class));

    return menu;
  }

  private Metrics getPreviousMetrics() {
    if (getParentAction().getPreviousBuildAction() != null) {
      List<RecipeAction> recipes =
          getParentAction().getPreviousBuildAction().getActions(RecipeAction.class);
      RecipeAction prevRecipe = recipes.stream().filter(
          o -> o.name.equals(this.name)).findFirst().orElse(null);
      if (prevRecipe != null) {
        return prevRecipe.getMetrics();
      }
    }
    return null;
  }

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public JSONObject getCodeSizeDeltaJson() {
    CodeSizeEvaluator previous =
        Optional.ofNullable(getPreviousMetrics())
            .map(Metrics::getCodeSize).orElse(null);
    CodeSizeEvaluator current = getMetrics().getCodeSize();
    return CodeSizeDelta.between(previous, current).toJsonObject().discard("recipes");
  }

  @Override
  public JSONObject getCodeSizeJson() {
    return this.getMetrics().getCodeSize().toJsonObject().discard("recipes");
  }
}
