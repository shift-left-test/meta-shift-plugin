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
import org.junit.Test;

/**
 * Unit tests for {@link FileSummaryTableModel}.
 */
public class FileSummaryTableModelTest {

  // DataSummary rows: name, total, first..fourth, ratio, qualified
  private static final JSONArray STATEMENTS = JSONArray.fromObject(
      "[{'name':'a.cpp','total':4,'first':2,'second':2,'ratio':0.5,'qualified':false},"
          + "{'name':'b.cpp','total':2,'first':2,'second':0,'ratio':1.0,'qualified':true}]");
  private static final JSONArray BRANCHES = JSONArray.fromObject(
      "[{'name':'a.cpp','total':4,'first':1,'second':3,'ratio':0.25,'qualified':false}]");
  private static final JSONArray MUTATIONS = JSONArray.fromObject(
      "[{'name':'b.cpp','total':1,'first':1,'second':0,'third':0,'ratio':1.0,'qualified':true}]");

  private FileSummaryTableModel newModel() {
    return new FileSummaryTableModel("files", STATEMENTS, BRANCHES, MUTATIONS);
  }

  @Test
  public void testColumns() {
    List<String> headers = newModel().getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("File", "Statement", "Branch", "Mutation", "Qualified"), headers);
    assertEquals("files", newModel().getId());
  }

  @Test
  public void testJoinsRowsByFileName() {
    List<Object> rows = newModel().getRows();
    assertEquals(2, rows.size());
    FileSummaryTableModel.FileRow first = (FileSummaryTableModel.FileRow) rows.get(0);
    assertEquals("a.cpp", first.getName().getSort());
    assertTrue(first.getName().getDisplay().contains("file?name=a.cpp"));
  }

  @Test
  public void testMetricCellShowsRatioAndCounts() {
    FileSummaryTableModel.FileRow row =
        (FileSummaryTableModel.FileRow) newModel().getRows().get(0);
    String display = row.getStatement().getDisplay();
    assertTrue(display.contains("50% (2/4)"));
    assertEquals(0.5, row.getStatement().getSort(), 0.001);
  }

  @Test
  public void testMissingMetricRendersDash() {
    FileSummaryTableModel.FileRow row =
        (FileSummaryTableModel.FileRow) newModel().getRows().get(0);
    assertEquals("–", row.getMutation().getDisplay());
    assertEquals(-1.0, row.getMutation().getSort(), 0.001);
  }

  @Test
  public void testQualifiedRequiresAllPresentMetricsQualified() {
    List<Object> rows = newModel().getRows();
    FileSummaryTableModel.FileRow a = (FileSummaryTableModel.FileRow) rows.get(0);
    FileSummaryTableModel.FileRow b = (FileSummaryTableModel.FileRow) rows.get(1);
    assertTrue(a.getQualified().getDisplay().contains("✘"));
    assertTrue(b.getQualified().getDisplay().contains("✔"));
  }

  @Test
  public void testNoExportButtons() {
    assertFalse(newModel().getTableConfiguration().isUseButtons());
  }
}
