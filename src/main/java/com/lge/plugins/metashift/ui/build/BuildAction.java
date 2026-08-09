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
import com.lge.plugins.metashift.ui.RawReportArchive;
import com.lge.plugins.metashift.ui.project.MetaShiftProjectAction;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import com.lge.plugins.metashift.ui.tables.EvaluationSummaryTableModel;
import com.lge.plugins.metashift.ui.tables.MutationListTableModel;
import com.lge.plugins.metashift.ui.tables.NativeTables;
import com.lge.plugins.metashift.ui.tables.TestListTableModel;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.datatables.AsyncTableContentProvider;
import io.jenkins.plugins.datatables.TableModel;
import java.io.File;
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
public class BuildAction extends Actionable
    implements LastBuildAction, RunAction2, AsyncTableContentProvider {

  /**
   * Name of the directory inside the build directory that holds the published report, and the
   * URL segment this action is served under.
   */
  public static final String STORAGE_DIR_NAME = "meta-shift-report";

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
    return STORAGE_DIR_NAME;
  }

  // implements RunAction2 API
  @Override
  public void onAttached(Run<?, ?> run) {
    this.run = run;
  }

  @Override
  public void onLoad(Run<?, ?> run) {
    this.run = run;
    relocateReports(run);
  }

  /**
   * Points every report of this build at the store inside the build directory it was just loaded
   * from.
   *
   * <p>The store's paths are persisted absolute, so they are stale for any build whose directory
   * has moved since it was published — a job rename, a move in or out of a folder, a different
   * {@code JENKINS_HOME}. The data moved with the build, so only the paths need correcting, and
   * this is the first point that knows where the build actually lives. The project report and
   * the recipe reports normally share one store, but they are rebound individually in case an
   * older record wrote separate copies.
   *
   * @param run this action belongs to
   */
  private void relocateReports(Run<?, ?> run) {
    File storage = new File(run.getRootDir(), STORAGE_DIR_NAME);
    if (projectReport != null) {
      projectReport.relocate(storage);
    }
    for (RecipeAction recipe : getActions(RecipeAction.class)) {
      if (recipe.getReport() != null) {
        recipe.getReport().relocate(storage);
      }
    }
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

  private transient RawReportArchive rawReportArchive;

  private RawReportArchive getRawReportArchive() {
    if (rawReportArchive == null) {
      rawReportArchive = new RawReportArchive(getRun().getRootDir());
    }
    return rawReportArchive;
  }

  /**
   * Tests whether any raw report of the given recipe is archived with this build, i.e. whether
   * the download link should be offered.
   *
   * @param reportDir report directory name of the recipe
   * @return true when the recipe has an archived report directory
   */
  public boolean hasRawReport(String reportDir) {
    return getRawReportArchive().hasDirectory(reportDir);
  }

  /**
   * Streams the whole archived report of one recipe as a zip, under
   * {@code meta-shift-report/download/&lt;reportDir&gt;}. Reachable only through this action, so
   * Jenkins has already checked Item/Read on the owning build.
   *
   * @param req stapler request; the rest of the path names the recipe report directory
   * @param rsp stapler response
   */
  public void doDownload(StaplerRequest2 req, StaplerResponse2 rsp)
      throws IOException, InterruptedException {
    getRawReportArchive().serveZip(req, rsp);
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
      String icon = Stapler.getCurrentRequest2().getContextPath()
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

  // persisted data is immutable after publish, so page-render reads are memoized
  private transient JSONArray failedTestSummaries;
  private transient JSONArray survivedMutationSummaries;
  private transient JSONObject listCounts;

  /**
   * Returns the failed/error unit tests of the whole build, for the build-wide Tests tab.
   *
   * @return failed/error TestData rows, each carrying its recipe name
   */
  private JSONArray getFailedTestSummaries() {
    if (failedTestSummaries == null) {
      failedTestSummaries = getReport().getFailedTests();
      if (failedTestSummaries.isEmpty() && !hasPrecomputedLists()) {
        failedTestSummaries = limited(collectFailedTestSummaries());
      }
    }
    return failedTestSummaries;
  }

  /**
   * Returns the survived mutations of the whole build, for the build-wide Mutations tab.
   *
   * @return survived MutationTestData rows, each carrying its recipe name
   */
  private JSONArray getSurvivedMutationSummaries() {
    if (survivedMutationSummaries == null) {
      survivedMutationSummaries = getReport().getSurvivedMutations();
      if (survivedMutationSummaries.isEmpty() && !hasPrecomputedLists()) {
        survivedMutationSummaries = limited(collectSurvivedMutationSummaries());
      }
    }
    return survivedMutationSummaries;
  }

  /**
   * Whether this build carries the project-wide lists computed while publishing.
   *
   * <p>Builds published before they existed do not, and fall back to reading every recipe's own
   * lists back from the store — slow, but the alternative is showing those builds empty tabs.
   *
   * @return true when the lists were precomputed
   */
  private boolean hasPrecomputedLists() {
    if (listCounts == null) {
      listCounts = getReport().getCounts();
    }
    return !listCounts.isEmpty();
  }

  private int totalOf(String key, JSONArray fallback) {
    return hasPrecomputedLists() ? listCounts.optInt(key) : fallback.size();
  }

  private JSONArray collectFailedTestSummaries() {
    JSONArray all = new JSONArray();
    for (RecipeAction recipe : getRecipes()) {
      for (Object o : recipe.getReport().getUnitTests().getSummaries()) {
        JSONObject row = (JSONObject) o;
        String status = row.optString("status");
        if ("FAILED".equals(status) || "ERROR".equals(status)) {
          all.add(row);
        }
      }
    }
    return all;
  }

  private JSONArray collectSurvivedMutationSummaries() {
    JSONArray all = new JSONArray();
    for (RecipeAction recipe : getRecipes()) {
      for (Object o : recipe.getReport().getMutationTests().getAllObjects()) {
        JSONObject row = (JSONObject) o;
        if ("SURVIVED".equals(row.optString("status"))) {
          all.add(row);
        }
      }
    }
    return all;
  }

  private static JSONArray limited(JSONArray array) {
    if (array.size() <= ProjectReportBuilder.MAX_LISTED_ROWS) {
      return array;
    }
    JSONArray limited = new JSONArray();
    for (int i = 0; i < ProjectReportBuilder.MAX_LISTED_ROWS; i++) {
      limited.add(array.get(i));
    }
    return limited;
  }

  /**
   * Number of failed/error unit tests across every recipe, before any capping.
   *
   * @return failed/error test count
   */
  public int getFailedTestCount() {
    return totalOf("failedTests", getFailedTestSummaries());
  }

  /**
   * Whether the build-wide Tests tab shows fewer rows than there are.
   *
   * @return true when rows were dropped
   */
  public boolean isFailedTestsTruncated() {
    return getFailedTestCount() > getFailedTestSummaries().size();
  }

  /**
   * Number of survived mutations across every recipe, before any capping.
   *
   * @return survived mutation count
   */
  public int getSurvivedMutationCount() {
    return totalOf("survivedMutations", getSurvivedMutationSummaries());
  }

  /**
   * Whether the build-wide Mutations tab shows fewer rows than there are.
   *
   * @return true when rows were dropped
   */
  public boolean isSurvivedMutationsTruncated() {
    return getSurvivedMutationCount() > getSurvivedMutationSummaries().size();
  }

  @Override
  public TableModel getTableModel(String id) {
    switch (id) {
      case "recipe-list":
        return new EvaluationSummaryTableModel(id, getRecipeSummaries());
      case "tests":
        return new TestListTableModel(id, getFailedTestSummaries(), file -> false, true);
      case "mutations":
        return new MutationListTableModel(id, getSurvivedMutationSummaries(), true);
      default:
        throw new IllegalArgumentException("Unknown table: " + id);
    }
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
