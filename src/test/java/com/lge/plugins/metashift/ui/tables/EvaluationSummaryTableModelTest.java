/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import static org.junit.Assert.assertEquals;
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

  private JSONObject metric(boolean available, double ratio) {
    JSONObject object = new JSONObject();
    object.put("available", available);
    object.put("ratio", ratio);
    return object;
  }

  private EvaluationSummaryTableModel model() {
    JSONObject row = new JSONObject();
    row.put("name", "A-1.0.0-r0");
    row.put("unitTests", metric(true, 0.9));
    row.put("statementCoverage", metric(true, 0.4));
    row.put("branchCoverage", metric(false, 0.0));
    row.put("mutationTests", metric(true, 0.16));
    JSONArray summaries = new JSONArray();
    summaries.add(row);
    return new EvaluationSummaryTableModel("recipe-list", summaries);
  }

  @Test
  public void testColumns() {
    List<String> headers = model().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipes", "Unit Tests", "Statement Coverage", "Branch Coverage",
        "Mutation Tests"), headers);
    assertEquals("recipe-list", model().getId());
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
