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
import com.lge.plugins.metashift.metrics.MetricStatistics;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.ui.MetricsActionBase;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.sf.json.JSONObject;
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
      Criteria criteria, FilePath reportRoot, Recipe recipe)
      throws IOException, InterruptedException {
    super(criteria, recipe);

    this.name = recipe.getRecipe();
    this.parent = parent;

    // parse metadata.json
    JSONObject metadata = JsonUtils.createObject(
        reportRoot.child(this.name).child("metadata.json"));

    listener.getLogger().println("Create shared state cache report");
    this.addAction(new RecipeSharedStateCacheAction(
        this, reportRoot.getChannel(), metadata, "Shared State Cache", "sharedstate_cache", true,
        listener, recipe));
    listener.getLogger().println("Create premirror cache report");
    this.addAction(new RecipePremirrorCacheAction(
        this, reportRoot.getChannel(), metadata, "Premirror Cache", "premirror_cache", true,
        listener, recipe));
    listener.getLogger().println("Create code violations report");
    this.addAction(new RecipeCodeViolationsAction(
        this, reportRoot.getChannel(), metadata, "Code Violations", "code_violations", false,
        listener, recipe));
    listener.getLogger().println("Create comments report");
    this.addAction(new RecipeCommentsAction(
        this, reportRoot.getChannel(), metadata, "Comments", "comments", true,
        listener, recipe));
    listener.getLogger().println("Create complexity report");
    this.addAction(new RecipeComplexityAction(
        this, reportRoot.getChannel(), metadata, "Complexity", "complexity", true,
        listener, recipe));
    listener.getLogger().println("Create statement coverage report");
    this.addAction(new RecipeStatementCoverageAction(
        this, reportRoot.getChannel(), metadata, "Statement Coverage", "statement_coverage", true,
        listener, recipe));
    listener.getLogger().println("Create branch coverage report");
    this.addAction(new RecipeBranchCoverageAction(
        this, reportRoot.getChannel(), metadata, "Branch Coverage", "branch_coverage", true,
        listener, recipe));
    listener.getLogger().println("Create duplications report");
    this.addAction(new RecipeDuplicationsAction(
        this, reportRoot.getChannel(), metadata, "Duplications", "duplications", true,
        listener, recipe));
    listener.getLogger().println("Create mutation test report");
    this.addAction(new RecipeMutationTestAction(
        this, reportRoot.getChannel(), metadata, "Mutation Test", "mutation_test", true,
        listener, recipe));
    listener.getLogger().println("Create recipe violations report");
    this.addAction(new RecipeRecipeViolationsAction(
        this, reportRoot.getChannel(), metadata, "Recipe Violations", "recipe_violations", false,
        listener, recipe));
    listener.getLogger().println("Create unit test report");
    this.addAction(new RecipeTestAction(
        this, reportRoot.getChannel(), metadata, "Test", "test", true,
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
