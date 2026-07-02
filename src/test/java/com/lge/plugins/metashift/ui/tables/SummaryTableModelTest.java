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
 * Unit tests for {@link SummaryTableModel}, {@link SummaryTableSpec} and {@link TableHtml}.
 */
public class SummaryTableModelTest {

  private JSONObject summary(String name, long total, long first, long second, double ratio,
      boolean qualified) {
    JSONObject object = new JSONObject();
    object.put("name", name);
    object.put("total", total);
    object.put("first", first);
    object.put("second", second);
    object.put("third", 0);
    object.put("fourth", 0);
    object.put("ratio", ratio);
    object.put("qualified", qualified);
    return object;
  }

  private SummaryTableModel model(JSONObject... rows) {
    JSONArray summaries = new JSONArray();
    for (JSONObject row : rows) {
      summaries.add(row);
    }
    return new SummaryTableModel(SummaryTableSpec.forBuildMetric("statement_coverage"), summaries);
  }

  @Test
  public void testColumns() {
    List<String> headers = model().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipe", "Statements", "Covered", "Uncovered", "Ratio", "Qualified"),
        headers);
  }

  @Test
  public void testMutationColumnsHaveThreeBuckets() {
    SummaryTableModel mutation =
        new SummaryTableModel(SummaryTableSpec.forBuildMetric("mutation_tests"), new JSONArray());
    List<String> headers = mutation.getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipe", "Tests", "Killed", "Survived", "Skipped", "Ratio", "Qualified"),
        headers);
  }

  @Test
  public void testIdAndExportButtons() {
    SummaryTableModel model = model();
    assertEquals("statement_coverage", model.getId());
    assertTrue(model.getTableConfiguration().isUseButtons());
  }

  @Test
  public void testRowMapping() {
    SummaryTableModel model = model(summary("A-1.0.0-r0", 10, 6, 4, 0.6, true));
    SummaryTableModel.SummaryRow row = (SummaryTableModel.SummaryRow) model.getRows().get(0);

    assertEquals("A-1.0.0-r0", row.getName().getSort());
    assertTrue(row.getName().getDisplay()
        .contains("href=\"../A-1.0.0-r0/statement_coverage\""));
    assertEquals(10, row.getTotal());
    assertEquals(6, row.getFirst());
    assertEquals(4, row.getSecond());
    assertEquals(0.6, row.getRatio().getSort(), 0.001);
    assertTrue(row.getRatio().getDisplay().contains("60%"));
    assertTrue(row.getQualified().getSort());
  }

  @Test
  public void testFileSpecLinksToFileView() {
    assertTrue(SummaryTableSpec.forRecipeFile("statement_coverage").nameAnchor("src/a.c")
        .contains(".?file=src%2Fa.c"));
  }

  @Test
  public void testHtmlHelpers() {
    assertEquals("&lt;a&gt;&amp;&quot;", TableHtml.escape("<a>&\""));
    assertTrue(TableHtml.progressBar(0.5).contains("width:50%"));
    assertTrue(TableHtml.progressBar(0.5).contains("50%"));
    assertTrue(TableHtml.qualifiedIcon(true).contains("text-success"));
    assertFalse(TableHtml.qualifiedIcon(false).contains("text-success"));
  }
}
