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
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
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

    this.addAction(this.childActionStatementCoverage = new BuildActionChild(
        this, this.getReport().getStatementCoverage(),
        "Statement Coverage", "statement_coverage", true));
    this.addAction(this.childActionBranchCoverage = new BuildActionChild(
        this, this.getReport().getBranchCoverage(),
        "Branch Coverage", "branch_coverage", true));
    this.addAction(this.childActionMutationTests = new BuildActionChild(
        this, this.getReport().getMutationTests(),
        "Mutation Tests", "mutation_tests", true));
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
  public ContextMenu doContextMenu(StaplerRequest2 request, StaplerResponse2 response)
      throws Exception {
    ContextMenu menu = super.doContextMenu(request, response);

    final MenuItem headerCodeQuality = new MenuItem().withDisplayName("Recipes");
    headerCodeQuality.type = MenuItemType.HEADER;
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
  public double getStatementCoverageDelta() {
    return getRatioDelta(ProjectReport::getStatementCoverage);
  }

  public double getBranchCoverageDelta() {
    return getRatioDelta(ProjectReport::getBranchCoverage);
  }

  public double getMutationTestDelta() {
    return getRatioDelta(ProjectReport::getMutationTests);
  }

  public double getTestDelta() {
    return getRatioDelta(ProjectReport::getUnitTests);
  }

  @Override
  public Collection<? extends Action> getProjectActions() {
    return Collections.singleton(new MetaShiftProjectAction(run));
  }
}
