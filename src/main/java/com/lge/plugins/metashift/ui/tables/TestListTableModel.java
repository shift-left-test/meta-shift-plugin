/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import io.jenkins.plugins.datatables.DetailedCell;
import io.jenkins.plugins.datatables.TableColumn;
import io.jenkins.plugins.datatables.TableColumn.ColumnBuilder;
import io.jenkins.plugins.datatables.TableColumn.ColumnType;
import io.jenkins.plugins.datatables.TableConfiguration;
import io.jenkins.plugins.datatables.TableModel;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A data-tables-api {@link TableModel} for the recipe unit-test list ({@code TestData} rows: suite,
 * test name, status and message). Rows are mapped from the existing {@code getSummaries()} JSON.
 */
public class TestListTableModel extends TableModel {

  private static final int MESSAGE_LIMIT = 300;

  private final String id;
  private final JSONArray summaries;

  public TestListTableModel(String id, JSONArray summaries) {
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
    columns.add(new ColumnBuilder().withHeaderLabel("Suite")
        .withDataPropertyKey("suite").withType(ColumnType.STRING).build());
    columns.add(new ColumnBuilder().withHeaderLabel("Name")
        .withDataPropertyKey("test").withType(ColumnType.STRING).build());
    columns.add(new ColumnBuilder().withHeaderLabel("Status")
        .withDataPropertyKey("status").withType(ColumnType.STRING).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Message")
        .withDataPropertyKey("message").withType(ColumnType.STRING).withDetailedCell().build());
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Object summary : summaries) {
      rows.add(new TestRow((JSONObject) summary));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration().buttons("excelHtml5", "csvHtml5");
  }

  /**
   * One unit-test row. Status renders as a colored Bootstrap badge; the message is HTML-escaped and
   * truncated for display while sorting/filtering on the raw text.
   */
  public static class TestRow {

    private final JSONObject data;

    TestRow(JSONObject data) {
      this.data = data;
    }

    public String getSuite() {
      return data.optString("suite");
    }

    public String getTest() {
      return data.optString("test");
    }

    public DetailedCell<String> getStatus() {
      String status = data.optString("status");
      return new DetailedCell<>(
          "<span class=\"badge " + badgeClass(status) + "\">" + TableHtml.escape(status) + "</span>",
          status);
    }

    public DetailedCell<String> getMessage() {
      String message = data.optString("message");
      String truncated = message.length() > MESSAGE_LIMIT
          ? message.substring(0, MESSAGE_LIMIT) + "..." : message;
      String display =
          "<span style=\"white-space:pre-wrap\">" + TableHtml.escape(truncated) + "</span>";
      return new DetailedCell<>(display, message);
    }

    private static String badgeClass(String status) {
      switch (status) {
        case "PASSED":
          return "bg-success";
        case "FAILED":
          return "bg-danger";
        case "ERROR":
          return "bg-warning";
        default:
          return "bg-secondary";
      }
    }
  }
}
