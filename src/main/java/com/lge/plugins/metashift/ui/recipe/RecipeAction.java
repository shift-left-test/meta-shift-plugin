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

import com.lge.plugins.metashift.builders.RecipeReport;
import com.lge.plugins.metashift.builders.RecipeReportBuilder;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.ActionParentBase;
import com.lge.plugins.metashift.ui.build.BuildAction;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;
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
public class RecipeAction extends ActionParentBase implements Action {

  BuildAction parent;

  @Exported(visibility = 999)
  public String name;

  private final RecipeReport recipeReport;

  /**
   * Default constructor.
   */
  public RecipeAction(BuildAction parent,
      Configuration configuration, DataSource dataSource,
      FilePath reportRoot, Recipe recipe)
      throws IOException, InterruptedException {
    super();

    this.name = recipe.getName();
    this.parent = parent;

    this.recipeReport = new RecipeReportBuilder(configuration, dataSource, reportRoot)
        .parse(recipe);

    this.addAction(this.childActionSharedStateCache = new RecipeActionChild(
        this, this.getReport().getSharedStateCache(),
        "Shared State Cache", "shared_state_cache", true));
    this.addAction(this.childActionPremirrorCache = new RecipeActionChild(
        this, this.getReport().getPremirrorCache(),
        "Premirror Cache", "premirror_cache", true));
    this.addAction(this.childActionCodeViolations = new RecipeActionChild(
        this, this.getReport().getCodeViolations(),
        "Code Violations", "code_violations", false));
    this.addAction(this.childActionComments = new RecipeActionChild(
        this, this.getReport().getComments(),
        "Comments", "comments", true));
    this.addAction(this.childActionComplexity = new RecipeActionChild(
        this, this.getReport().getComplexity(),
        "Complexity", "complexity", true));
    this.addAction(this.childActionStatementCoverage = new RecipeActionChild(
        this, this.getReport().getStatementCoverage(),
        "Statement Coverage", "statement_coverage", true));
    this.addAction(this.childActionBranchCoverage = new RecipeActionChild(
        this, this.getReport().getBranchCoverage(),
        "Branch Coverage", "branch_coverage", true));
    this.addAction(this.childActionDuplications = new RecipeActionChild(
        this, this.getReport().getDuplications(),
        "Duplications", "duplications", true));
    this.addAction(this.childActionMutationTests = new RecipeActionChild(
        this, this.getReport().getMutationTests(),
        "Mutation Tests", "mutation_tests", true));
    this.addAction(this.childActionRecipeViolations = new RecipeActionChild(
        this, this.getReport().getRecipeViolations(),
        "Recipe Violations", "recipe_violations", false));
    this.addAction(this.childActionUnitTests = new RecipeActionChild(
        this, this.getReport().getUnitTests(),
        "Unit Tests", "unit_tests", true));
  }

  public BuildAction getParentAction() {
    return this.parent;
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

  @Override
  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public RecipeReport getReport() {
    return this.recipeReport;
  }

  private RecipeReport getPreviousReport() {
    if (getParentAction().getPreviousBuildAction() != null) {
      List<RecipeAction> recipes =
          getParentAction().getPreviousBuildAction().getActions(RecipeAction.class);
      RecipeAction prevRecipe = recipes.stream()
          .filter(o -> o.name.equals(this.name)).findFirst().orElse(null);
      if (prevRecipe != null) {
        return prevRecipe.getReport();
      }
    }
    return null;
  }

  public JSONObject getCodeSizeJson() {
    return this.getReport().getLinesOfCode().discard("recipes");
  }

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public JSONObject getCodeSizeDeltaJson() {
    JSONObject previous =
        Optional.ofNullable(getPreviousReport())
            .map(RecipeReport::getLinesOfCode).orElse(null);
    JSONObject current = getReport().getLinesOfCode();
    if (previous == null) {
      return current.discard("recipes");
    }
    JSONObject delta = new JSONObject();
    delta.put("lines", current.getLong("lines") - previous.getLong("lines"));
    delta.put("functions", current.getLong("functions") - previous.getLong("functions"));
    delta.put("classes", current.getLong("classes") - previous.getLong("classes"));
    delta.put("files", current.getLong("files") - previous.getLong("files"));

    return delta;
  }

}
