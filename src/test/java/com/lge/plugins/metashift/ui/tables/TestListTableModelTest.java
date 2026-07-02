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
    return new TestListTableModel("unit_tests", summaries);
  }

  @Test
  public void testColumns() {
    List<String> headers = model().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Suite", "Name", "Status", "Message"), headers);
  }

  @Test
  public void testStatusBadge() {
    TestListTableModel.TestRow passed =
        (TestListTableModel.TestRow) model(test("s", "t", "PASSED", "")).getRows().get(0);
    assertEquals("PASSED", passed.getStatus().getSort());
    assertTrue(passed.getStatus().getDisplay().contains("bg-success"));

    TestListTableModel.TestRow failed =
        (TestListTableModel.TestRow) model(test("s", "t", "FAILED", "")).getRows().get(0);
    assertTrue(failed.getStatus().getDisplay().contains("bg-danger"));
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
