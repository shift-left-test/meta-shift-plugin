/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import io.jenkins.plugins.datatables.DetailedCell;
import io.jenkins.plugins.datatables.TableColumn;
import io.jenkins.plugins.datatables.TableColumn.ColumnBuilder;
import io.jenkins.plugins.datatables.TableColumn.ColumnCss;
import io.jenkins.plugins.datatables.TableColumn.ColumnType;
import io.jenkins.plugins.datatables.TableConfiguration;
import io.jenkins.plugins.datatables.TableModel;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A data-tables-api {@link TableModel} for the {@code DataSummary} tables (build recipe tables and
 * recipe file tables). The columns and name-link are driven by a {@link SummaryTableSpec}; the rows
 * are mapped from the existing precomputed {@code getSummaries()} JSON without touching the backend
 * data model.
 */
public class SummaryTableModel extends TableModel {

  private final SummaryTableSpec spec;
  private final JSONArray summaries;

  public SummaryTableModel(SummaryTableSpec spec, JSONArray summaries) {
    this.spec = spec;
    this.summaries = summaries;
  }

  @Override
  public String getId() {
    return spec.getId();
  }

  @Override
  public List<TableColumn> getColumns() {
    List<TableColumn> columns = new ArrayList<>();
    columns.add(new ColumnBuilder().withHeaderLabel(spec.getNameHeader())
        .withDataPropertyKey("name").withType(ColumnType.STRING).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel(spec.getTotalHeader())
        .withDataPropertyKey("total").withType(ColumnType.FORMATTED_NUMBER)
        .withHeaderClass(ColumnCss.NUMBER).build());
    for (SummaryTableSpec.Bucket bucket : spec.getBuckets()) {
      columns.add(new ColumnBuilder().withHeaderLabel(bucket.getHeader())
          .withDataPropertyKey(bucket.getKey()).withType(ColumnType.FORMATTED_NUMBER)
          .withHeaderClass(ColumnCss.NUMBER).build());
    }
    columns.add(new ColumnBuilder().withHeaderLabel("Ratio")
        .withDataPropertyKey("ratio").withType(ColumnType.NUMBER)
        .withHeaderClass(ColumnCss.PERCENTAGE).withDetailedCell().build());
    columns.add(new ColumnBuilder().withHeaderLabel("Qualified")
        .withDataPropertyKey("qualified").withType(ColumnType.STRING)
        .withHeaderClass(ColumnCss.NO_SORT).withDetailedCell().build());
    return columns;
  }

  @Override
  public List<Object> getRows() {
    List<Object> rows = new ArrayList<>();
    for (Object summary : summaries) {
      rows.add(new SummaryRow((JSONObject) summary, spec));
    }
    return rows;
  }

  @Override
  public TableConfiguration getTableConfiguration() {
    return new TableConfiguration().buttons("excelHtml5", "csvHtml5");
  }

  /**
   * One row of a {@link SummaryTableModel}, serialized to JSON by Jackson over its public getters.
   * The property names match the {@link TableColumn} data property keys.
   */
  public static class SummaryRow {

    private final JSONObject data;
    private final SummaryTableSpec spec;

    SummaryRow(JSONObject data, SummaryTableSpec spec) {
      this.data = data;
      this.spec = spec;
    }

    public DetailedCell<String> getName() {
      String name = data.getString("name");
      return new DetailedCell<>(spec.nameAnchor(name), name);
    }

    public long getTotal() {
      return data.optLong("total");
    }

    public long getFirst() {
      return data.optLong("first");
    }

    public long getSecond() {
      return data.optLong("second");
    }

    public long getThird() {
      return data.optLong("third");
    }

    public long getFourth() {
      return data.optLong("fourth");
    }

    public DetailedCell<Double> getRatio() {
      double ratio = data.getDouble("ratio");
      return new DetailedCell<>(TableHtml.progressBar(ratio), ratio);
    }

    public DetailedCell<Boolean> getQualified() {
      boolean qualified = data.getBoolean("qualified");
      return new DetailedCell<>(TableHtml.qualifiedIcon(qualified), qualified);
    }
  }
}
