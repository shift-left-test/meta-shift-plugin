/*
 * Copyright (c) 2026 LG Electronics Inc.
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A data-tables-api {@link TableModel} for a mutation list ({@code MutationTestData} rows:
 * file, line, class/method, mutator, status and killing test). Rows are mapped from
 * {@code RecipeGroup.getAllObjects()} of the mutation tests metric, either for a single recipe
 * (the recipe page) or concatenated across recipes (the build page).
 */
public class MutationListTableModel extends TableModel {

  private final String id;
  private final JSONArray objects;
  private final boolean includeRecipeColumn;

  /**
   * Default constructor, for a single recipe's mutation list.
   *
   * @param id      table id
   * @param objects MutationTestData rows of one recipe
   */
  public MutationListTableModel(String id, JSONArray objects) {
    this(id, objects, false);
  }

  /**
   * Default constructor.
   *
   * @param id                  table id
   * @param objects             MutationTestData rows, possibly spanning multiple recipes
   * @param includeRecipeColumn true to add a Recipe column linking to each row's recipe page,
   *                            for the project-wide list on the build page
   */
  public MutationListTableModel(String id, JSONArray objects, boolean includeRecipeColumn) {
    this.id = id;
    this.objects = objects;
    this.includeRecipeColumn = includeRecipeColumn;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<TableColumn> getColumns() {
    List<TableColumn> columns = new ArrayList<>();
    if (includeRecipeColumn) {
      columns.add(new ColumnBuilder().withHeaderLabel("Recipe")
          .withDataPropertyKey("recipe").withType(ColumnType.STRING).withDetailedCell().build());
    }
    columns.add(new ColumnBuilder().withHeaderLabel("File")
        .withDataPropertyKey("file").withType(ColumnType.STRING).build());
    columns.add(new ColumnBuilder().withHeaderLabel("Line")
        .withDataPropertyKey("line").withType(ColumnType.NUMBER).build());
    columns.add(new ColumnBuilder().withHeaderLabel("Method")
        .withDataPropertyKey("method").withType(ColumnType.STRING).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Mutator")
        .withDataPropertyKey("mutator").withType(ColumnType.STRING).build());
    columns.add(new ColumnBuilder().withHeaderLabel("Status")
        .withDataPropertyKey("status").withType(ColumnType.NUMBER).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Killing Test")
        .withDataPropertyKey("killingTest").withType(ColumnType.STRING).build());
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Object object : objects) {
      rows.add(new MutationRow((JSONObject) object));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration();
  }

  /**
   * One mutation row. Status renders as a colored badge and sorts by severity rank (survived
   * first, matching the "needs attention" ordering used elsewhere in the plugin).
   */
  public static class MutationRow {

    private final JSONObject data;

    MutationRow(JSONObject data) {
      this.data = data;
    }

    /**
     * Recipe name cell, linking to that recipe's page. Only rendered when the table includes
     * the Recipe column (the project-wide list on the build page).
     */
    public DetailedCell<String> getRecipe() {
      String recipe = data.optString("name");
      return new DetailedCell<>(
          TableHtml.anchor(Util.rawEncode(recipe) + "/", recipe), recipe);
    }

    public String getFile() {
      return data.optString("file");
    }

    public long getLine() {
      return data.optLong("line");
    }

    public DetailedCell<String> getMethod() {
      String mutatedClass = data.optString("mutatedClass");
      String mutatedMethod = data.optString("mutatedMethod");
      String qualified = mutatedClass.isEmpty() ? mutatedMethod
          : mutatedClass + "::" + mutatedMethod;
      return new DetailedCell<>(TableHtml.escape(qualified), qualified);
    }

    public String getMutator() {
      return data.optString("mutator");
    }

    public DetailedCell<Integer> getStatus() {
      String status = data.optString("status");
      return new DetailedCell<>(
          "<span class=\"badge " + badgeClass(status) + "\">" + TableHtml.escape(status)
              + "</span>",
          rank(status));
    }

    public String getKillingTest() {
      return data.optString("killingTest");
    }

    private static String badgeClass(String status) {
      switch (status) {
        case "SURVIVED":
          return "text-bg-danger";
        case "KILLED":
          return "text-bg-success";
        default:
          return "text-bg-secondary";
      }
    }

    private static int rank(String status) {
      switch (status) {
        case "SURVIVED":
          return 0;
        case "SKIPPED":
          return 1;
        default:
          return 2;
      }
    }
  }
}
