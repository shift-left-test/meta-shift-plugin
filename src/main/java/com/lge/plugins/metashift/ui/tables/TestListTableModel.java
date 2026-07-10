/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import hudson.Util;
import io.jenkins.plugins.datatables.DetailedCell;
import io.jenkins.plugins.datatables.TableColumn;
import io.jenkins.plugins.datatables.TableColumn.ColumnBuilder;
import io.jenkins.plugins.datatables.TableColumn.ColumnType;
import io.jenkins.plugins.datatables.TableConfiguration;
import io.jenkins.plugins.datatables.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
  private final Predicate<String> hasSource;

  /**
   * Default constructor.
   *
   * @param id        table id
   * @param summaries TestData rows
   * @param hasSource tests whether the source of a file is stored with the build
   */
  public TestListTableModel(String id, JSONArray summaries, Predicate<String> hasSource) {
    this.id = id;
    this.summaries = summaries;
    this.hasSource = hasSource;
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
    // Second column: the data-tables default order ([[1,'asc']]) sorts the numeric
    // status rank, surfacing FAILED/ERROR rows first.
    columns.add(new ColumnBuilder().withHeaderLabel("Status")
        .withDataPropertyKey("status").withType(ColumnType.NUMBER).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Name")
        .withDataPropertyKey("test").withType(ColumnType.STRING).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Message")
        .withDataPropertyKey("message").withType(ColumnType.STRING).withDetailedCell().build());
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Object summary : summaries) {
      rows.add(new TestRow((JSONObject) summary, hasSource));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration();
  }

  /**
   * One unit-test row. Status renders as a colored badge and sorts by severity rank (failed
   * first); the message is HTML-escaped and truncated for display while sorting/filtering on the
   * raw text.
   */
  public static class TestRow {

    private final JSONObject data;
    private final Predicate<String> hasSource;

    TestRow(JSONObject data, Predicate<String> hasSource) {
      this.data = data;
      this.hasSource = hasSource;
    }

    public String getSuite() {
      return data.optString("suite");
    }

    /**
     * Test name cell: appends a source link ({@code file:line}) when the source of the
     * file is stored with the build.
     */
    public DetailedCell<String> getTest() {
      String test = data.optString("test");
      String file = data.optString("file");
      long line = data.optLong("line");
      String display = TableHtml.escape(test);
      if (!file.isEmpty() && hasSource.test(file)) {
        String href = "file?name=" + Util.rawEncode(file) + (line > 0 ? "#L" + line : "");
        String label = TableHtml.escape(file) + (line > 0 ? ":" + line : "");
        display += " <a class=\"small font-monospace\" href=\"" + href + "\">" + label + "</a>";
      }
      return new DetailedCell<>(display, test);
    }

    public DetailedCell<Integer> getStatus() {
      String status = data.optString("status");
      return new DetailedCell<>(
          "<span class=\"badge " + badgeClass(status) + "\">" + TableHtml.escape(status)
              + "</span>",
          rank(status));
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

    private static int rank(String status) {
      switch (status) {
        case "FAILED":
          return 0;
        case "ERROR":
          return 1;
        case "PASSED":
          return 3;
        default:
          return 2;
      }
    }
  }
}
