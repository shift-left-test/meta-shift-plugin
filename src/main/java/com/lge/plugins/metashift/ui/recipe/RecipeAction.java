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
import com.lge.plugins.metashift.ui.MetricView;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.tables.FileSummaryTableModel;
import com.lge.plugins.metashift.ui.tables.NativeTables;
import com.lge.plugins.metashift.ui.tables.TestListTableModel;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;
import io.jenkins.plugins.datatables.AsyncTableContentProvider;
import io.jenkins.plugins.datatables.TableModel;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * The recipe action class: a single consolidated page with the metric cards on
 * top, the unit test list and the unified per-file table, plus the annotated
 * source page of each file.
 */
@ExportedBean
public class RecipeAction implements Action, AsyncTableContentProvider {

  BuildAction parent;

  @Exported(visibility = 999)
  public String name;

  private final RecipeReport recipeReport;

  /**
   * Default constructor.
   */
  public RecipeAction(BuildAction parent, Configuration configuration, DataSource dataSource,
      FilePath reportRoot, Recipe recipe) throws IOException, InterruptedException {
    this.name = recipe.getName();
    this.parent = parent;

    this.recipeReport =
        new RecipeReportBuilder(configuration, dataSource, reportRoot).parse(recipe);
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

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public RecipeReport getReport() {
    return this.recipeReport;
  }

  /**
   * Returns the metric summary cards for the four surviving metrics.
   *
   * @return list of metric cards
   */
  public List<MetricView> getMetricCards() {
    return MetricView.cardsFor(getReport(), getTestDelta(),
        getStatementCoverageDelta(), getBranchCoverageDelta(), getMutationTestDelta());
  }

  // persisted data is immutable after publish, so page-render reads are memoized
  private transient JSONArray testSummaries;
  private transient FileSummaryTableModel fileSummaryModel;

  private JSONArray getTestSummaries() {
    if (testSummaries == null) {
      testSummaries = getReport().getUnitTests().getSummaries();
    }
    return testSummaries;
  }

  private FileSummaryTableModel getFileSummaryModel() {
    if (fileSummaryModel == null) {
      RecipeReport report = getReport();
      fileSummaryModel = new FileSummaryTableModel("files",
          report.getStatementCoverage().getSummaries(),
          report.getBranchCoverage().getSummaries(),
          report.getMutationTests().getSummaries());
    }
    return fileSummaryModel;
  }

  @Override
  public TableModel getTableModel(String id) {
    if ("unit_tests".equals(id)) {
      RecipeReport report = getReport();
      Map<String, Boolean> stored = new HashMap<>();
      return new TestListTableModel(id, getTestSummaries(),
          file -> stored.computeIfAbsent(file, report::hasFile));
    }
    if ("files".equals(id)) {
      return getFileSummaryModel();
    }
    throw new IllegalArgumentException("Unknown table: " + id);
  }

  /**
   * Returns the number of unit tests of this recipe.
   *
   * @return test count
   */
  public int getTestCount() {
    return getTestSummaries().size();
  }

  /**
   * Returns the number of failed or error unit tests of this recipe.
   *
   * @return failed test count
   */
  public long getFailedTestCount() {
    return getTestSummaries().stream()
        .map(o -> ((JSONObject) o).optString("status"))
        .filter(status -> "FAILED".equals(status) || "ERROR".equals(status))
        .count();
  }

  /**
   * Returns the number of files known to any file-scoped metric.
   *
   * @return file count
   */
  public int getFileCount() {
    return getFileSummaryModel().getFileCount();
  }

  @Override
  @JavaScriptMethod
  public String getTableRows(String id) {
    return NativeTables.toJson(getTableModel(id).getRows());
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

  private Double getRatioDelta(Function<RecipeReport, RecipeGroup> mapper) {
    RecipeGroup previous = Optional.ofNullable(getPreviousReport()).map(mapper).orElse(null);
    RecipeGroup current = mapper.apply(getReport());
    if (current == null || previous == null) {
      return null;
    }
    return current.getEvaluation().getDouble("ratio")
        - previous.getEvaluation().getDouble("ratio");
  }

  // ratio delta, null when there is no reference build
  public Double getStatementCoverageDelta() {
    return getRatioDelta(RecipeReport::getStatementCoverage);
  }

  public Double getBranchCoverageDelta() {
    return getRatioDelta(RecipeReport::getBranchCoverage);
  }

  public Double getMutationTestDelta() {
    return getRatioDelta(RecipeReport::getMutationTests);
  }

  public Double getTestDelta() {
    return getRatioDelta(RecipeReport::getUnitTests);
  }

  /**
   * Returns the view model of the requested file, or null when unknown.
   *
   * @return file detail view
   */
  public FileDetailView getFileDetail() {
    String file = Stapler.getCurrentRequest2().getParameter("name");
    if (file == null || file.isEmpty()) {
      return null;
    }
    RecipeReport report = getReport();
    return FileDetailView.of(file,
        FileDetailView.MetricData.of(
            report.getStatementCoverage().getObjects(file),
            report.getStatementCoverage().getSummaries()),
        FileDetailView.MetricData.of(
            report.getBranchCoverage().getObjects(file),
            report.getBranchCoverage().getSummaries()),
        FileDetailView.MetricData.of(
            report.getMutationTests().getObjects(file),
            report.getMutationTests().getSummaries()));
  }

  /**
   * Returns the annotated source view of the requested file, or null when the source is
   * not stored with this build (the page then falls back to the annotation summary).
   *
   * @return source annotation view or null
   */
  public SourceAnnotationView getSourceView() {
    String file = Stapler.getCurrentRequest2().getParameter("name");
    if (file == null || file.isEmpty()) {
      return null;
    }
    RecipeReport report = getReport();
    return SourceAnnotationView.of(file, report.readFile(file),
        report.getStatementCoverage().getObjects(file),
        report.getBranchCoverage().getObjects(file),
        report.getMutationTests().getObjects(file));
  }

  /**
   * Renders the per-file annotated source page.
   */
  public void doFile(StaplerRequest2 req, StaplerResponse2 res)
      throws ServletException, IOException {
    req.getView(this, "file.jelly").forward(req, res);
  }
}
