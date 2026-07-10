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
import com.lge.plugins.metashift.ui.MetricView;
import com.lge.plugins.metashift.ui.project.MetaShiftProjectAction;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import com.lge.plugins.metashift.ui.tables.EvaluationSummaryTableModel;
import com.lge.plugins.metashift.ui.tables.NativeTables;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.Actionable;
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
import io.jenkins.plugins.datatables.AsyncTableContentProvider;
import io.jenkins.plugins.datatables.TableModel;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;
import net.sf.json.JSONArray;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * The main post build action class.
 */
@ExportedBean
public class BuildAction extends Actionable
    implements LastBuildAction, RunAction2, AsyncTableContentProvider {

  private transient Run<?, ?> run;
  private transient List<RecipeAction> recipeActions;

  private final ProjectReport projectReport;

  /**
   * Default constructor.
   */
  public BuildAction(Run<?, ?> run, TaskListener listener, Configuration configuration,
      DataSource dataSource, FilePath reportRoot, Recipes recipes)
      throws IOException, InterruptedException {
    this.run = run;

    this.projectReport = new ProjectReportBuilder(configuration, dataSource).parse(recipes);

    listener.getLogger().println("[meta-shift-plugin] Publishing the meta-shift results...");

    for (Recipe recipe : recipes) {
      listener.getLogger().printf("[meta-shift-plugin] -> %s%n", recipe.getName());
      RecipeAction recipeAction =
          new RecipeAction(this, configuration, dataSource, reportRoot, recipe);
      this.addAction(recipeAction);
    }

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
  public Run<?, ?> getRun() {
    return this.run;
  }

  public ProjectReport getReport() {
    return this.projectReport;
  }

  private transient List<MetricView> metricCards;

  /**
   * Returns the metric summary cards for the four surviving metrics. The persisted
   * data is immutable after publish, so the cards are computed once per load.
   *
   * @return list of metric cards
   */
  public List<MetricView> getMetricCards() {
    if (metricCards == null) {
      metricCards = MetricView.cardsFor(getReport(), getTestDelta(),
          getStatementCoverageDelta(), getBranchCoverageDelta(), getMutationTestDelta());
    }
    return metricCards;
  }

  private static final int MAX_ATTENTION_RECIPES = 5;

  // persisted data is immutable after publish, so page-render reads are memoized
  private transient JSONArray recipeSummaries;

  private JSONArray getRecipeSummaries() {
    if (recipeSummaries == null) {
      recipeSummaries = getReport().getSummaries();
    }
    return recipeSummaries;
  }

  /**
   * Returns the "recipes needing attention" view for the overview page.
   *
   * @return attention view
   */
  public AttentionView getAttention() {
    return AttentionView.of(getRecipeSummaries(), MAX_ATTENTION_RECIPES);
  }

  /**
   * Returns the number of recipes of this report.
   *
   * @return recipe count
   */
  public int getRecipeCount() {
    return getRecipeSummaries().size();
  }

  /**
   * Number of metrics with a report available.
   *
   * @return available metric count
   */
  public long getAvailableCount() {
    return getMetricCards().stream().filter(MetricView::isAvailable).count();
  }

  /**
   * Number of available metrics that satisfy their threshold.
   *
   * @return qualified metric count
   */
  public long getQualifiedCount() {
    return getMetricCards().stream().filter(MetricView::isAvailable)
        .filter(MetricView::isQualified).count();
  }

  /**
   * Available metrics that do not satisfy their threshold, for the summary badges.
   *
   * @return failed metric cards
   */
  public List<MetricView> getFailedCards() {
    return getMetricCards().stream().filter(MetricView::isAvailable)
        .filter(card -> !card.isQualified()).collect(Collectors.toList());
  }

  /**
   * context menu provider.
   */
  public ContextMenu doContextMenu(StaplerRequest2 request, StaplerResponse2 response)
      throws Exception {
    ContextMenu menu = new ContextMenu();

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

  @Override
  public TableModel getTableModel(String id) {
    return new EvaluationSummaryTableModel(id, getRecipeSummaries());
  }

  @Override
  @JavaScriptMethod
  public String getTableRows(String id) {
    return NativeTables.toJson(getTableModel(id).getRows());
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

  private Double getRatioDelta(Function<ProjectReport, ProjectGroup> mapper) {
    ProjectGroup previous = Optional.ofNullable(getPreviousReport()).map(mapper).orElse(null);
    ProjectGroup current = mapper.apply(getReport());
    if (current == null || previous == null) {
      return null;
    }
    return current.getEvaluation().getDouble("ratio")
        - previous.getEvaluation().getDouble("ratio");
  }

  // ratio delta, null when there is no reference build
  public Double getStatementCoverageDelta() {
    return getRatioDelta(ProjectReport::getStatementCoverage);
  }

  public Double getBranchCoverageDelta() {
    return getRatioDelta(ProjectReport::getBranchCoverage);
  }

  public Double getMutationTestDelta() {
    return getRatioDelta(ProjectReport::getMutationTests);
  }

  public Double getTestDelta() {
    return getRatioDelta(ProjectReport::getUnitTests);
  }

  @Override
  public Collection<? extends Action> getProjectActions() {
    return Collections.singleton(new MetaShiftProjectAction(run));
  }
}
