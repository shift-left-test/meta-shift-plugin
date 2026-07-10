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
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A data-tables-api {@link TableModel} for the unified per-file table of the recipe page:
 * one row per file with a statement/branch/mutation cell each, joined from the three
 * per-metric {@code DataSummary} arrays at render time.
 */
public class FileSummaryTableModel extends TableModel {

  private static final int STATEMENT = 0;
  private static final int BRANCH = 1;
  private static final int MUTATION = 2;
  private static final int METRICS = 3;

  private final String id;
  private final Map<String, JSONObject[]> byFile;

  /**
   * Default constructor.
   *
   * @param id         table id
   * @param statements statement coverage DataSummary rows
   * @param branches   branch coverage DataSummary rows
   * @param mutations  mutation test DataSummary rows
   */
  public FileSummaryTableModel(String id, JSONArray statements, JSONArray branches,
      JSONArray mutations) {
    this.id = id;
    this.byFile = new TreeMap<>();
    join(statements, STATEMENT);
    join(branches, BRANCH);
    join(mutations, MUTATION);
  }

  private void join(JSONArray summaries, int index) {
    for (Object o : summaries) {
      JSONObject row = (JSONObject) o;
      byFile.computeIfAbsent(row.optString("name"), k -> new JSONObject[METRICS])[index] = row;
    }
  }

  @Override
  public String getId() {
    return id;
  }

  /**
   * Returns the number of joined files.
   *
   * @return file count
   */
  public int getFileCount() {
    return byFile.size();
  }

  @Override
  public List<TableColumn> getColumns() {
    List<TableColumn> columns = new ArrayList<>();
    columns.add(new ColumnBuilder().withHeaderLabel("File")
        .withDataPropertyKey("name").withType(ColumnType.STRING).withDetailedCell().build());
    for (String[] metric : new String[][]{
        {"Statement", "statement"}, {"Branch", "branch"}, {"Mutation", "mutation"}}) {
      columns.add(new ColumnBuilder().withHeaderLabel(metric[0])
          .withDataPropertyKey(metric[1]).withType(ColumnType.NUMBER)
          .withHeaderClass(ColumnCss.PERCENTAGE).withDetailedCell().build());
    }
    columns.add(new ColumnBuilder().withHeaderLabel("Qualified")
        .withDataPropertyKey("qualified").withType(ColumnType.NUMBER).withDetailedCell().build());
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Map.Entry<String, JSONObject[]> entry : byFile.entrySet()) {
      rows.add(new FileRow(entry.getKey(), entry.getValue()));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration();
  }

  /**
   * One file row: a name link plus one cell per metric ({@code "–"} when the file has no
   * data for the metric) and an overall qualified indicator.
   */
  public static class FileRow {

    private final String name;
    private final JSONObject[] metrics;

    FileRow(String name, JSONObject[] metrics) {
      this.name = name;
      this.metrics = metrics;
    }

    public DetailedCell<String> getName() {
      return new DetailedCell<>(
          TableHtml.anchor("file?name=" + Util.rawEncode(name), name), name);
    }

    public DetailedCell<Double> getStatement() {
      return metricCell(metrics[STATEMENT]);
    }

    public DetailedCell<Double> getBranch() {
      return metricCell(metrics[BRANCH]);
    }

    public DetailedCell<Double> getMutation() {
      return metricCell(metrics[MUTATION]);
    }

    private DetailedCell<Double> metricCell(JSONObject metric) {
      if (metric == null) {
        return new DetailedCell<>("–", -1.0);
      }
      double ratio = metric.optDouble("ratio", 0);
      boolean qualified = metric.optBoolean("qualified", false);
      String label = TableHtml.percent(ratio) + "% (" + metric.optLong("first") + "/"
          + metric.optLong("total") + ")";
      return new DetailedCell<>(TableHtml.progressBar(ratio, qualified, label), ratio);
    }

    /**
     * Overall qualification: every metric present for this file must be qualified.
     */
    public DetailedCell<Integer> getQualified() {
      boolean qualified = true;
      for (JSONObject metric : metrics) {
        if (metric != null && !metric.optBoolean("qualified", false)) {
          qualified = false;
        }
      }
      return new DetailedCell<>(TableHtml.qualifiedIcon(qualified), qualified ? 0 : 1);
    }
  }
}
