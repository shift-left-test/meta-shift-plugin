/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.jenkins.plugins.datatables.TableColumn;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for {@link TestListTableModel}.
 */
public class TestListTableModelTest {

  private JSONObject test(String suite, String name, String status, String message) {
    JSONObject object = new JSONObject();
    object.put("suite", suite);
    object.put("test", name);
    object.put("status", status);
    object.put("message", message);
    return object;
  }

  private TestListTableModel model(JSONObject... tests) {
    JSONArray summaries = new JSONArray();
    for (JSONObject t : tests) {
      summaries.add(t);
    }
    return new TestListTableModel("unit_tests", summaries, file -> true);
  }

  private JSONObject testWithSource(String name, String file, long line) {
    JSONObject object = test("s", name, "PASSED", "");
    object.put("file", file);
    object.put("line", line);
    return object;
  }

  @Test
  public void testColumns() {
    List<String> headers = model().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    // Status is the second column: data-tables sorts by column index 1 by default,
    // so failed/error rows surface first via the numeric status rank.
    assertEquals(List.of("Suite", "Status", "Name", "Message"), headers);
  }

  @Test
  public void testStatusBadgeAndRank() {
    TestListTableModel.TestRow passed =
        (TestListTableModel.TestRow) model(test("s", "t", "PASSED", "")).getRows().get(0);
    assertEquals(Integer.valueOf(3), passed.getStatus().getSort());
    assertTrue(passed.getStatus().getDisplay().contains("bg-success"));
    assertTrue(passed.getStatus().getDisplay().contains("PASSED"));

    TestListTableModel.TestRow failed =
        (TestListTableModel.TestRow) model(test("s", "t", "FAILED", "")).getRows().get(0);
    assertEquals(Integer.valueOf(0), failed.getStatus().getSort());
    assertTrue(failed.getStatus().getDisplay().contains("bg-danger"));

    TestListTableModel.TestRow error =
        (TestListTableModel.TestRow) model(test("s", "t", "ERROR", "")).getRows().get(0);
    assertEquals(Integer.valueOf(1), error.getStatus().getSort());

    TestListTableModel.TestRow skipped =
        (TestListTableModel.TestRow) model(test("s", "t", "SKIPPED", "")).getRows().get(0);
    assertEquals(Integer.valueOf(2), skipped.getStatus().getSort());
  }

  @Test
  public void testNameCellLinksToStoredSource() {
    JSONArray rows = new JSONArray();
    rows.add(testWithSource("t1", "src/a.cpp", 28));
    TestListTableModel model = new TestListTableModel("unit_tests", rows, file -> true);
    TestListTableModel.TestRow row = (TestListTableModel.TestRow) model.getRows().get(0);
    assertEquals("t1", row.getTest().getSort());
    String display = row.getTest().getDisplay();
    assertTrue(display.contains("t1"));
    assertTrue(display.contains("href=\"file?name=src%2Fa.cpp#L28\""));
    assertTrue(display.contains("src/a.cpp:28"));
  }

  @Test
  public void testNameCellHasNoLinkWhenSourceNotStored() {
    JSONArray rows = new JSONArray();
    rows.add(testWithSource("t1", "src/a.cpp", 28));
    TestListTableModel model = new TestListTableModel("unit_tests", rows, file -> false);
    TestListTableModel.TestRow row = (TestListTableModel.TestRow) model.getRows().get(0);
    assertFalse(row.getTest().getDisplay().contains("href"));
  }

  @Test
  public void testNameCellHasNoLinkWithoutFile() {
    TestListTableModel.TestRow row =
        (TestListTableModel.TestRow) model(test("s", "t1", "PASSED", "")).getRows().get(0);
    assertFalse(row.getTest().getDisplay().contains("href"));
  }

  @Test
  public void testNoExportButtons() {
    assertFalse(model().getTableConfiguration().isUseButtons());
  }

  @Test
  public void testMessageEscapedAndTruncated() {
    String longMessage = "<b>" + "x".repeat(400);
    TestListTableModel.TestRow row =
        (TestListTableModel.TestRow) model(test("s", "t", "ERROR", longMessage)).getRows().get(0);

    assertEquals(longMessage, row.getMessage().getSort());
    String display = row.getMessage().getDisplay();
    assertTrue(display.contains("&lt;b&gt;"));
    assertFalse(display.contains("<b>"));
    assertTrue(display.contains("..."));
  }
}
