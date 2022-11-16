/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import com.lge.plugins.metashift.builders.ProjectGroup;
import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.builders.ProjectReportBuilder;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.ActionParentBase;
import com.lge.plugins.metashift.ui.project.MetaShiftProjectAction;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * The main post build action class.
 */
@ExportedBean
public class BuildAction extends ActionParentBase implements LastBuildAction, RunAction2 {

  private transient Run<?, ?> run;
  private transient List<RecipeAction> recipeActions;

  private final ProjectReport projectReport;

  /**
   * Default constructor.
   */
  public BuildAction(Run<?, ?> run, TaskListener listener,
      Configuration configuration, DataSource dataSource,
      FilePath reportRoot, Recipes recipes)
      throws IOException, InterruptedException {
    super();

    this.run = run;

    this.projectReport = new ProjectReportBuilder(configuration, dataSource).parse(recipes);

    listener.getLogger().println("[meta-shift-plugin] Publishing the meta-shift results...");

    for (Recipe recipe : recipes) {
      listener.getLogger().printf("[meta-shift-plugin] -> %s%n", recipe.getName());
      RecipeAction recipeAction = new RecipeAction(
          this, configuration, dataSource, reportRoot, recipe);
      this.addAction(recipeAction);
    }

    this.addAction(this.childActionSharedStateCache = new BuildActionChild(
        this, this.getReport().getSharedStateCache(),
        "Shared State Cache", "shared_state_cache", true));
    this.addAction(this.childActionPremirrorCache = new BuildActionChild(
        this, this.getReport().getPremirrorCache(),
        "Premirror Cache", "premirror_cache", true));
    this.addAction(this.childActionCodeViolations = new BuildActionChild(
        this, this.getReport().getCodeViolations(),
        "Code Violations", "code_violations", false));
    this.addAction(this.childActionComments = new BuildActionChild(
        this, this.getReport().getComments(),
        "Comments", "comments", true));
    this.addAction(this.childActionComplexity = new BuildActionChild(
        this, this.getReport().getComplexity(),
        "Complexity", "complexity", true));
    this.addAction(this.childActionStatementCoverage = new BuildActionChild(
        this, this.getReport().getStatementCoverage(),
        "Statement Coverage", "statement_coverage", true));
    this.addAction(this.childActionBranchCoverage = new BuildActionChild(
        this, this.getReport().getBranchCoverage(),
        "Branch Coverage", "branch_coverage", true));
    this.addAction(this.childActionDuplications = new BuildActionChild(
        this, this.getReport().getDuplications(),
        "Duplications", "duplications", true));
    this.addAction(this.childActionMutationTests = new BuildActionChild(
        this, this.getReport().getMutationTests(),
        "Mutation Tests", "mutation_tests", true));
    this.addAction(this.childActionRecipeViolations = new BuildActionChild(
        this, this.getReport().getRecipeViolations(),
        "Recipe Violations", "recipe_violations", false));
    this.addAction(this.childActionUnitTests = new BuildActionChild(
        this, this.getReport().getUnitTests(),
        "Unit Tests", "unit_tests", true));
    listener.getLogger().println("[meta-shift-plugin] Successfully published.");
  }

  @Override
  public String getIconFileName() {
    return "/plugin/meta-shift/img/meta_shift_first.png";
  }

  @Override
  public String getDisplayName() {
    return "meta-shift report";
  }

  @Override
  public String getUrlName() {
    return "meta-shift-report";
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

  /**
   * Returns the run object which generated this action.
   *
   * @return Run class
   */
  @Override
  public Run<?, ?> getRun() {
    return this.run;
  }

  public ProjectReport getReport() {
    return this.projectReport;
  }

  /**
   * context menu provider.
   */
  public ContextMenu doContextMenu(StaplerRequest request, StaplerResponse response)
      throws Exception {
    ContextMenu menu = super.doContextMenu(request, response);

    final MenuItem headerCodeQuality = new MenuItem().withDisplayName("Recipes");
    headerCodeQuality.header = true;
    menu.add(headerCodeQuality);
    // TODO: RecipeAction::getDisplayName may return null.
    List<RecipeAction> actions = this.getActions(RecipeAction.class).stream()
        .sorted(Comparator.comparing(RecipeAction::getDisplayName)).collect(Collectors.toList());

    for (Action a : actions) {
      String base = Functions.getIconFilePath(a);
      String icon = Stapler.getCurrentRequest().getContextPath()
          + (base.startsWith("images/") ? Functions.getResourcePath() : "")
          + '/' + base;

      menu.add(a.getUrlName(), icon, a.getDisplayName());
    }

    return menu;
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
    JSONObject model = new JSONObject();

    model.put("data", this.getReport().getTreemap());
    model.put("tooltipInfo", this.getReport().getSummaries());

    return model;
  }

  /**
   * return paginated recipes list.
   *
   * @return recipe qualifier list.
   */
  @JavaScriptMethod
  public JSONArray getRecipesTableModel() throws InterruptedException {
    return this.getReport().getSummaries();
  }

  /**
   * return recipe count which has available test.
   *
   * @return tested recipe count
   */
  public long getTestedRecipes() {
    return this.projectReport.getTestedRecipes().getLong("numerator");
  }

  private transient BuildAction previousAction;

  /**
   * return previous not failed build action.
   *
   * @return metashift build action
   */
  public BuildAction getPreviousBuildAction() {
    if (previousAction != null) {
      return previousAction;
    }

    Run<?, ?> build = this.run.getPreviousNotFailedBuild();
    while (build != null) {
      if (build.getResult() != Result.FAILURE) {
        previousAction = build.getAction(BuildAction.class);
        if (previousAction != null) {
          return previousAction;
        }
      }
      build = build.getPreviousNotFailedBuild();
    }
    return null;
  }

  private ProjectReport getPreviousReport() {
    if (getPreviousBuildAction() != null) {
      return getPreviousBuildAction().getReport();
    } else {
      return null;
    }
  }

  /**
   * return tested recipe rate change.
   */
  public double getTestedRecipesDelta() {
    BuildAction previous = getPreviousBuildAction();
    double previousRatio = (previous == null) ? 0 :
        previous.getReport().getTestedRecipes().getDouble("ratio");
    return getReport().getTestedRecipes().getDouble("ratio") - previousRatio;
  }

  public JSONObject getCodeSizeJson() {
    return this.getReport().getLinesOfCode();
  }

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public JSONObject getCodeSizeDeltaJson() {
    JSONObject previous =
        Optional.ofNullable(getPreviousReport()).map(ProjectReport::getLinesOfCode).orElse(null);
    JSONObject current = getReport().getLinesOfCode();
    if (previous == null) {
      return current;
    }
    JSONObject delta = new JSONObject();
    delta.put("lines", current.getLong("lines") - previous.getLong("lines"));
    delta.put("functions", current.getLong("functions") - previous.getLong("functions"));
    delta.put("classes", current.getLong("classes") - previous.getLong("classes"));
    delta.put("files", current.getLong("files") - previous.getLong("files"));
    delta.put("recipes", current.getLong("recipes") - previous.getLong("recipes"));

    return delta;
  }

  private double getRatioDelta(Function<ProjectReport, ProjectGroup> mapper) {
    ProjectGroup previous = Optional.ofNullable(getPreviousReport()).map(mapper).orElse(null);
    ProjectGroup current = mapper.apply(getReport());
    if (current == null) {
      return 0;
    }
    return (previous != null) ? current.getEvaluation().getDouble("ratio")
        - previous.getEvaluation().getDouble("ratio") : current.getEvaluation().getDouble("ratio");
  }

  // ratio delta
  public double getPremirrorCacheDelta() {
    return getRatioDelta(ProjectReport::getPremirrorCache);
  }

  public double getSharedStateCacheDelta() {
    return getRatioDelta(ProjectReport::getSharedStateCache);
  }

  public double getCodeViolationsDelta() {
    return getRatioDelta(ProjectReport::getCodeViolations);
  }

  public double getCommentsDelta() {
    return getRatioDelta(ProjectReport::getComments);
  }

  public double getComplexityDelta() {
    return getRatioDelta(ProjectReport::getComplexity);
  }

  public double getStatementCoverageDelta() {
    return getRatioDelta(ProjectReport::getStatementCoverage);
  }

  public double getBranchCoverageDelta() {
    return getRatioDelta(ProjectReport::getBranchCoverage);
  }

  public double getDuplicationsDelta() {
    return getRatioDelta(ProjectReport::getDuplications);
  }

  public double getMutationTestDelta() {
    return getRatioDelta(ProjectReport::getMutationTests);
  }

  public double getRecipeViolationsDelta() {
    return getRatioDelta(ProjectReport::getRecipeViolations);
  }

  public double getTestDelta() {
    return getRatioDelta(ProjectReport::getUnitTests);
  }

  @Override
  public Collection<? extends Action> getProjectActions() {
    return Collections.singleton(new MetaShiftProjectAction(run));
  }
}
