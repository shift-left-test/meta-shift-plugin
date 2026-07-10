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
 * Unit tests for {@link EvaluationSummaryTableModel}.
 */
public class EvaluationSummaryTableModelTest {

  private JSONObject metric(boolean available, double ratio, boolean qualified) {
    JSONObject object = new JSONObject();
    object.put("available", available);
    object.put("ratio", ratio);
    object.put("qualified", qualified);
    return object;
  }

  private EvaluationSummaryTableModel model() {
    JSONObject row = new JSONObject();
    row.put("name", "A-1.0.0-r0");
    row.put("unitTests", metric(true, 0.9, true));
    row.put("statementCoverage", metric(true, 0.4, false));
    row.put("branchCoverage", metric(false, 0.0, false));
    row.put("mutationTests", metric(true, 0.16, false));
    JSONArray summaries = new JSONArray();
    summaries.add(row);
    return new EvaluationSummaryTableModel("recipe-list", summaries);
  }

  @Test
  public void testColumns() {
    List<String> headers = model().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipes", "Unit Tests", "Statement Coverage", "Branch Coverage",
        "Mutation Tests", "Qualified"), headers);
    assertEquals("recipe-list", model().getId());
  }

  @Test
  public void testQualifiedCellCountsFailedMetrics() {
    EvaluationSummaryTableModel.EvaluationRow row =
        (EvaluationSummaryTableModel.EvaluationRow) model().getRows().get(0);

    // 3 available metrics, 1 qualified -> "✘ 1/3", sorts by the failed count
    assertEquals(Integer.valueOf(2), row.getQualified().getSort());
    assertTrue(row.getQualified().getDisplay().contains("1/3"));
    assertTrue(row.getQualified().getDisplay().contains("text-danger"));
  }

  @Test
  public void testQualifiedCellWithNoAvailableMetricRendersNa() {
    JSONObject row = new JSONObject();
    row.put("name", "A-1.0.0-r0");
    JSONArray summaries = new JSONArray();
    summaries.add(row);
    EvaluationSummaryTableModel model = new EvaluationSummaryTableModel("id", summaries);
    EvaluationSummaryTableModel.EvaluationRow first =
        (EvaluationSummaryTableModel.EvaluationRow) model.getRows().get(0);
    assertEquals("N/A", first.getQualified().getDisplay());
    assertEquals(Integer.valueOf(-1), first.getQualified().getSort());
  }

  @Test
  public void testNoExportButtons() {
    assertFalse(model().getTableConfiguration().isUseButtons());
  }

  @Test
  public void testRowMapping() {
    EvaluationSummaryTableModel.EvaluationRow row =
        (EvaluationSummaryTableModel.EvaluationRow) model().getRows().get(0);

    assertEquals("A-1.0.0-r0", row.getName().getSort());
    assertTrue(row.getName().getDisplay().contains("href=\"A-1.0.0-r0\""));

    assertEquals(0.9, row.getUnitTests().getSort(), 0.001);
    assertTrue(row.getUnitTests().getDisplay().contains("90%"));
  }

  @Test
  public void testUnavailableMetricRendersNa() {
    EvaluationSummaryTableModel.EvaluationRow row =
        (EvaluationSummaryTableModel.EvaluationRow) model().getRows().get(0);

    // branchCoverage is unavailable -> N/A display, sort value below any real ratio
    assertEquals("N/A", row.getBranchCoverage().getDisplay());
    assertEquals(-1.0, row.getBranchCoverage().getSort(), 0.001);
  }
}
