/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import hudson.Util;
import io.jenkins.plugins.datatables.DetailedCell;
import io.jenkins.plugins.datatables.TableColumn;
import io.jenkins.plugins.datatables.TableColumn.ColumnBuilder;
import io.jenkins.plugins.datatables.TableColumn.ColumnCss;
import io.jenkins.plugins.datatables.TableColumn.ColumnType;
import io.jenkins.plugins.datatables.TableConfiguration;
import io.jenkins.plugins.datatables.TableModel;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A data-tables-api {@link TableModel} for the build overview recipe list: one row per recipe with
 * a qualification-ratio column for each of the four metrics. Rows are mapped from the existing
 * {@code EvaluationSummary} JSON produced by {@code getSummaries()} without changing the backend.
 */
public class EvaluationSummaryTableModel extends TableModel {

  private static final class Metric {

    private final String header;
    private final String key;

    private Metric(String header, String key) {
      this.header = header;
      this.key = key;
    }
  }

  private static final List<Metric> METRICS = List.of(
      new Metric("Unit Tests", "unitTests"),
      new Metric("Statement Coverage", "statementCoverage"),
      new Metric("Branch Coverage", "branchCoverage"),
      new Metric("Mutation Tests", "mutationTests"));

  private final String id;
  private final JSONArray summaries;

  public EvaluationSummaryTableModel(String id, JSONArray summaries) {
    this.id = id;
    this.summaries = summaries;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<TableColumn> getColumns() {
    List<TableColumn> columns = new ArrayList<>();
    columns.add(new ColumnBuilder().withHeaderLabel("Recipes")
        .withDataPropertyKey("name").withType(ColumnType.STRING).withDetailedCell().build());
    for (Metric metric : METRICS) {
      columns.add(new ColumnBuilder().withHeaderLabel(metric.header)
          .withDataPropertyKey(metric.key).withType(ColumnType.NUMBER)
          .withHeaderClass(ColumnCss.PERCENTAGE).withDetailedCell().build());
    }
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Object summary : summaries) {
      rows.add(new EvaluationRow((JSONObject) summary));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration().buttons("excelHtml5", "csvHtml5");
  }

  /**
   * One row of the overview table. The name links to the recipe page (relative to the build page);
   * each metric cell shows a progress bar or {@code N/A} when the metric is unavailable.
   */
  public static class EvaluationRow {

    private final JSONObject data;

    EvaluationRow(JSONObject data) {
      this.data = data;
    }

    public DetailedCell<String> getName() {
      String name = data.getString("name");
      String anchor = TableHtml.anchor(Util.rawEncode(name), name);
      return new DetailedCell<>(anchor, name);
    }

    public DetailedCell<Double> getUnitTests() {
      return metricCell("unitTests");
    }

    public DetailedCell<Double> getStatementCoverage() {
      return metricCell("statementCoverage");
    }

    public DetailedCell<Double> getBranchCoverage() {
      return metricCell("branchCoverage");
    }

    public DetailedCell<Double> getMutationTests() {
      return metricCell("mutationTests");
    }

    private DetailedCell<Double> metricCell(String key) {
      JSONObject metric = data.optJSONObject(key);
      if (metric != null && metric.optBoolean("available", false)) {
        double ratio = metric.getDouble("ratio");
        return new DetailedCell<>(TableHtml.progressBar(ratio), ratio);
      }
      // Unavailable metrics render as N/A and sort below any real ratio.
      return new DetailedCell<>("N/A", -1.0);
    }
  }
}
