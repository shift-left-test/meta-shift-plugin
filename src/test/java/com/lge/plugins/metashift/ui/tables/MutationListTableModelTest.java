/*
 * Copyright (c) 2026 LG Electronics Inc.
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
 * Unit tests for {@link MutationListTableModel}.
 */
public class MutationListTableModelTest {

  private JSONObject mutation(String recipe, String file, long line, String mutatedClass,
      String mutatedMethod, String mutator, String status, String killingTest) {
    JSONObject object = new JSONObject();
    object.put("name", recipe);
    object.put("file", file);
    object.put("line", line);
    object.put("mutatedClass", mutatedClass);
    object.put("mutatedMethod", mutatedMethod);
    object.put("mutator", mutator);
    object.put("status", status);
    object.put("killingTest", killingTest);
    return object;
  }

  private MutationListTableModel model(boolean includeRecipeColumn, JSONObject... mutations) {
    JSONArray objects = new JSONArray();
    for (JSONObject m : mutations) {
      objects.add(m);
    }
    return new MutationListTableModel("mutations", objects, includeRecipeColumn);
  }

  @Test
  public void testRecipeColumnOmittedByDefault() {
    List<String> headers = new MutationListTableModel("mutations", new JSONArray())
        .getColumns().stream().map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("File", "Line", "Method", "Mutator", "Status", "Killing Test"),
        headers);
  }

  @Test
  public void testRecipeColumnAddedWhenRequested() {
    List<String> headers = model(true).getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipe", "File", "Line", "Method", "Mutator", "Status",
        "Killing Test"), headers);
  }

  @Test
  public void testRowMapping() {
    JSONObject killed = mutation("recipe-A", "a.cpp", 12, "A", "foo", "AOR", "KILLED", "test1");
    MutationListTableModel.MutationRow row =
        (MutationListTableModel.MutationRow) model(true, killed).getRows().get(0);

    assertEquals("recipe-A", row.getRecipe().getSort());
    assertTrue(row.getRecipe().getDisplay().contains("href=\"recipe-A/\""));
    assertEquals("a.cpp", row.getFile().getSort());
    assertEquals(12, row.getLine());
    assertEquals("A::foo", row.getMethod().getSort());
    assertEquals("AOR", row.getMutator().getSort());
    assertEquals("test1", row.getKillingTest().getSort());
  }

  @Test
  public void testMethodCellWithoutClassOmitsSeparator() {
    JSONObject mutation = mutation("recipe-A", "a.cpp", 1, "", "main", "UOI", "SURVIVED", "");
    MutationListTableModel.MutationRow row =
        (MutationListTableModel.MutationRow) model(false, mutation).getRows().get(0);
    assertEquals("main", row.getMethod().getSort());
  }

  @Test
  public void testStatusBadgeAndRank() {
    MutationListTableModel.MutationRow survived = (MutationListTableModel.MutationRow) model(
        false, mutation("r", "f", 1, "C", "m", "AOR", "SURVIVED", "")).getRows().get(0);
    assertEquals(Integer.valueOf(0), survived.getStatus().getSort());
    assertTrue(survived.getStatus().getDisplay().contains("text-bg-danger"));

    MutationListTableModel.MutationRow skipped = (MutationListTableModel.MutationRow) model(
        false, mutation("r", "f", 1, "C", "m", "AOR", "SKIPPED", "")).getRows().get(0);
    assertEquals(Integer.valueOf(1), skipped.getStatus().getSort());
    assertTrue(skipped.getStatus().getDisplay().contains("text-bg-secondary"));

    MutationListTableModel.MutationRow killed = (MutationListTableModel.MutationRow) model(
        false, mutation("r", "f", 1, "C", "m", "AOR", "KILLED", "t")).getRows().get(0);
    assertEquals(Integer.valueOf(2), killed.getStatus().getSort());
    assertTrue(killed.getStatus().getDisplay().contains("text-bg-success"));
  }

  @Test
  public void testNoExportButtons() {
    assertFalse(model(false).getTableConfiguration().isUseButtons());
  }

  @Test
  public void testFileCellEscapesMarkupAndSortsOnRawValue() {
    String raw = "<img src=x onerror=alert(1)>a.cpp";
    Object row = model(false,
        mutation("r", raw, 1, "C", "m", "AOR", "KILLED", "t")).getRows().get(0);

    assertEquals("&lt;img src=x onerror=alert(1)&gt;a.cpp", CellValues.html(row, "file"));
    assertEquals(raw, CellValues.sort(row, "file"));
  }

  @Test
  public void testMutatorCellEscapesMarkupAndSortsOnRawValue() {
    String raw = "<img src=x onerror=alert(1)>AOR";
    Object row = model(false,
        mutation("r", "a.cpp", 1, "C", "m", raw, "KILLED", "t")).getRows().get(0);

    assertEquals("&lt;img src=x onerror=alert(1)&gt;AOR", CellValues.html(row, "mutator"));
    assertEquals(raw, CellValues.sort(row, "mutator"));
  }

  @Test
  public void testKillingTestCellEscapesMarkupAndSortsOnRawValue() {
    String raw = "<img src=x onerror=alert(1)>test1";
    Object row = model(false,
        mutation("r", "a.cpp", 1, "C", "m", "AOR", "KILLED", raw)).getRows().get(0);

    assertEquals("&lt;img src=x onerror=alert(1)&gt;test1", CellValues.html(row, "killingTest"));
    assertEquals(raw, CellValues.sort(row, "killingTest"));
  }

  @Test
  public void testRecipeCellLinksToTheVersionStrippedRecipePage() {
    Object row = model(true, mutation("autotools-project-1.0.0-r0", "a.cpp", 1, "C", "m", "AOR",
        "KILLED", "t")).getRows().get(0);

    assertEquals("<a href=\"autotools-project/\">autotools-project</a>",
        CellValues.html(row, "recipe"));
    assertEquals("autotools-project", CellValues.sort(row, "recipe"));
  }
}
