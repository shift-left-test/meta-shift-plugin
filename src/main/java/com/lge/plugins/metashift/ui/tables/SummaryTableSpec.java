/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import hudson.Util;
import java.util.List;
import java.util.Map;

/**
 * Immutable configuration for a {@link SummaryTableModel}. The build recipe tables and the recipe
 * file tables share the same {@code DataSummary} row shape ({@code name,total,first..fourth,ratio,
 * qualified}); they differ only in the name-column header, the per-metric bucket headers, and where
 * the name cell links.
 */
public final class SummaryTableSpec {

  /**
   * Where the name cell of each row links.
   */
  private enum LinkKind {
    /** build recipe table: links to the per-recipe metric page ({@code ../<name>/<metric>}). */
    BUILD_METRIC_RECIPE,
    /** recipe file table: links to the file view on the same page ({@code .?file=<name>}). */
    RECIPE_FILE
  }

  /**
   * A distribution bucket column: the {@code DataSummary} JSON key and its localized header.
   */
  public static final class Bucket {

    private final String key;
    private final String header;

    public Bucket(String key, String header) {
      this.key = key;
      this.header = header;
    }

    public String getKey() {
      return key;
    }

    public String getHeader() {
      return header;
    }
  }

  private static final class Shape {

    private final String totalHeader;
    private final List<Bucket> buckets;

    private Shape(String totalHeader, List<Bucket> buckets) {
      this.totalHeader = totalHeader;
      this.buckets = buckets;
    }
  }

  private static final Map<String, Shape> SHAPES = Map.of(
      "statement_coverage", new Shape("Statements",
          List.of(new Bucket("first", "Covered"), new Bucket("second", "Uncovered"))),
      "branch_coverage", new Shape("Branches",
          List.of(new Bucket("first", "Covered"), new Bucket("second", "Uncovered"))),
      "mutation_tests", new Shape("Tests",
          List.of(new Bucket("first", "Killed"), new Bucket("second", "Survived"),
              new Bucket("third", "Skipped"))),
      "unit_tests", new Shape("Tests",
          List.of(new Bucket("first", "Passed"), new Bucket("second", "Failed"),
              new Bucket("third", "Error"), new Bucket("fourth", "Skipped"))));

  private final String id;
  private final String nameHeader;
  private final String totalHeader;
  private final List<Bucket> buckets;
  private final LinkKind linkKind;
  private final String metricUrl;

  private SummaryTableSpec(String id, String nameHeader, String totalHeader, List<Bucket> buckets,
      LinkKind linkKind, String metricUrl) {
    this.id = id;
    this.nameHeader = nameHeader;
    this.totalHeader = totalHeader;
    this.buckets = buckets;
    this.linkKind = linkKind;
    this.metricUrl = metricUrl;
  }

  /**
   * Returns the spec for a build recipe table (rows are recipes linking to the metric page).
   *
   * @param metricUrl metric url segment (e.g. {@code statement_coverage})
   * @return the spec
   */
  public static SummaryTableSpec forBuildMetric(String metricUrl) {
    Shape shape = shapeOf(metricUrl);
    return new SummaryTableSpec(metricUrl, "Recipe", shape.totalHeader, shape.buckets,
        LinkKind.BUILD_METRIC_RECIPE, metricUrl);
  }

  /**
   * Returns the spec for a recipe file table (rows are files linking to the file view).
   *
   * @param metricUrl metric url segment (e.g. {@code statement_coverage})
   * @return the spec
   */
  public static SummaryTableSpec forRecipeFile(String metricUrl) {
    Shape shape = shapeOf(metricUrl);
    return new SummaryTableSpec(metricUrl, "File", shape.totalHeader, shape.buckets,
        LinkKind.RECIPE_FILE, metricUrl);
  }

  private static Shape shapeOf(String metricUrl) {
    Shape shape = SHAPES.get(metricUrl);
    if (shape == null) {
      throw new IllegalArgumentException("No summary table shape for metric: " + metricUrl);
    }
    return shape;
  }

  public String getId() {
    return id;
  }

  public String getNameHeader() {
    return nameHeader;
  }

  public String getTotalHeader() {
    return totalHeader;
  }

  public List<Bucket> getBuckets() {
    return buckets;
  }

  /**
   * Builds the HTML anchor shown in the name cell for the given row name.
   *
   * @param name recipe or file name
   * @return anchor HTML
   */
  public String nameAnchor(String name) {
    String encoded = Util.rawEncode(name);
    String href = linkKind == LinkKind.BUILD_METRIC_RECIPE
        ? "../" + encoded + "/" + metricUrl
        : ".?file=" + encoded;
    return "<a href=\"" + href + "\">" + TableHtml.escape(name) + "</a>";
  }
}
