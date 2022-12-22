/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.builders.RecipeGroup;
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
import java.util.function.Function;
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

  private double getRatioDelta(Function<RecipeReport, RecipeGroup> mapper) {
    RecipeGroup previous = Optional.ofNullable(getPreviousReport()).map(mapper).orElse(null);
    RecipeGroup current = mapper.apply(getReport());
    if (current == null) {
      return 0;
    }
    return (previous != null) ? current.getEvaluation().getDouble("ratio")
        - previous.getEvaluation().getDouble("ratio") : current.getEvaluation().getDouble("ratio");
  }

  public double getPremirrorCacheDelta() {
    return getRatioDelta(RecipeReport::getPremirrorCache);
  }

  public double getSharedStateCacheDelta() {
    return getRatioDelta(RecipeReport::getSharedStateCache);
  }

  public double getCodeViolationsDelta() {
    return getRatioDelta(RecipeReport::getCodeViolations);
  }

  public double getCommentsDelta() {
    return getRatioDelta(RecipeReport::getComments);
  }

  public double getComplexityDelta() {
    return getRatioDelta(RecipeReport::getComplexity);
  }

  public double getStatementCoverageDelta() {
    return getRatioDelta(RecipeReport::getStatementCoverage);
  }

  public double getBranchCoverageDelta() {
    return getRatioDelta(RecipeReport::getBranchCoverage);
  }

  public double getDuplicationsDelta() {
    return getRatioDelta(RecipeReport::getDuplications);
  }

  public double getMutationTestDelta() {
    return getRatioDelta(RecipeReport::getMutationTests);
  }

  public double getRecipeViolationsDelta() {
    return getRatioDelta(RecipeReport::getRecipeViolations);
  }

  public double getTestDelta() {
    return getRatioDelta(RecipeReport::getUnitTests);
  }
}
