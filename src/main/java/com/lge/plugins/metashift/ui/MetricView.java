/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import com.lge.plugins.metashift.builders.Group;
import com.lge.plugins.metashift.builders.Report;
import com.lge.plugins.metashift.ui.tables.TableHtml;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

/**
 * Server-side view model for a single metric summary card.
 *
 * <p>Formats the persisted evaluation/distribution JSON into display strings so
 * the card can be rendered natively from Jelly. The distribution renders as a
 * stacked bar, replacing the former per-metric page's distribution header.</p>
 *
 * @author Sung Gon Kim
 */
public class MetricView {

  /**
   * Distribution band labels and bootstrap colours per metric url.
   * Each entry maps the first/second/third/fourth distribution scales in order.
   */
  private static final Map<String, String[][]> BANDS = Map.of(
      "unit_tests", new String[][]{
          {"Passed", "success"}, {"Failed", "danger"}, {"Error", "warning"},
          {"Skipped", "secondary"}},
      "statement_coverage", new String[][]{
          {"Hits", "success"}, {"Misses", "secondary"}},
      "branch_coverage", new String[][]{
          {"Hits", "success"}, {"Misses", "secondary"}},
      "mutation_tests", new String[][]{
          {"Killed", "success"}, {"Survived", "danger"}, {"Skipped", "secondary"}});

  private static final String[] SCALE_KEYS = {"first", "second", "third", "fourth"};

  private final String name;
  private final String url;
  private final JSONObject evaluation;
  private final JSONObject distribution;
  private final Double delta;

  /**
   * Default constructor.
   *
   * @param name         metric display name
   * @param url          metric url segment, used as the section anchor and band key
   * @param evaluation   evaluation result JSON
   * @param distribution distribution result JSON
   * @param delta        build-over-build ratio delta, null when there is no reference build
   */
  public MetricView(String name, String url, JSONObject evaluation, JSONObject distribution,
      Double delta) {
    this.name = name;
    this.url = url;
    this.evaluation = evaluation;
    this.distribution = distribution;
    this.delta = delta;
  }

  /**
   * Builds the cards for the four surviving metrics of the given report.
   *
   * @param report         report to read the metric groups from
   * @param testDelta      unit test ratio delta
   * @param statementDelta statement coverage ratio delta
   * @param branchDelta    branch coverage ratio delta
   * @param mutationDelta  mutation test ratio delta
   * @return ordered list of metric cards
   */
  public static List<MetricView> cardsFor(Report<? extends Group> report, Double testDelta,
      Double statementDelta, Double branchDelta, Double mutationDelta) {
    return Arrays.asList(
        of("Unit Tests", "unit_tests", report.getUnitTests(), testDelta),
        of("Statement Coverage", "statement_coverage", report.getStatementCoverage(),
            statementDelta),
        of("Branch Coverage", "branch_coverage", report.getBranchCoverage(), branchDelta),
        of("Mutation Tests", "mutation_tests", report.getMutationTests(), mutationDelta));
  }

  private static MetricView of(String name, String url, Group group, Double delta) {
    return new MetricView(name, url, group.getEvaluation(), group.getDistribution(), delta);
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  /**
   * Recipe page section anchor of this metric: unit tests have their own section, the
   * three file-scoped metrics share the Files section.
   *
   * @return section anchor id
   */
  public String getSectionAnchor() {
    return "unit_tests".equals(url) ? "unit_tests" : "files";
  }

  public boolean isAvailable() {
    return evaluation.optBoolean("available", false);
  }

  public boolean isQualified() {
    return evaluation.optBoolean("qualified", false);
  }

  /**
   * Bootstrap contextual suffix used for the badge and fallback bar colour.
   *
   * @return "success", "danger" or "secondary"
   */
  public String getBarClass() {
    if (!isAvailable()) {
      return "secondary";
    }
    return isQualified() ? "success" : "danger";
  }

  /**
   * Status label shown alongside the badge: "N/A", "PASS" or "FAIL".
   *
   * @return status text
   */
  public String getStatusText() {
    if (!isAvailable()) {
      return "N/A";
    }
    return isQualified() ? "PASS" : "FAIL";
  }

  /**
   * Main ratio as a percent string, or "N/A" when unavailable.
   *
   * @return ratio text
   */
  public String getRatioText() {
    return isAvailable() ? TableHtml.percent(evaluation.optDouble("ratio", 0)) + "%" : "N/A";
  }

  /**
   * Integer ratio percent (0-100) for the fallback progress bar width.
   *
   * @return ratio percent
   */
  public int getRatioPercent() {
    return isAvailable() ? TableHtml.percent(evaluation.optDouble("ratio", 0)) : 0;
  }

  /**
   * Whether this metric has no reference build to compare against.
   *
   * <p>Named to avoid the JEXL {@code new} keyword in jelly ({@code card.newMetric}).</p>
   *
   * @return true when the delta cannot be computed
   */
  public boolean isNewMetric() {
    return isAvailable() && delta == null;
  }

  /**
   * Build-over-build delta as an arrow and percent, empty when zero/unavailable/new.
   *
   * @return delta text
   */
  public String getDeltaText() {
    if (!isAvailable() || delta == null || delta == 0) {
      return "";
    }
    String arrow = delta > 0 ? "▲" : "▼";
    return arrow + " " + String.format("%.1f%%", Math.abs(delta * 100));
  }

  public String getNumerator() {
    return String.format("%,d", evaluation.optLong("numerator"));
  }

  public String getDenominator() {
    return String.format("%,d", evaluation.optLong("denominator"));
  }

  public String getThresholdText() {
    return TableHtml.percent(evaluation.optDouble("threshold", 0)) + "%";
  }

  /**
   * Whether a non-empty distribution is available and the metric url has a band mapping.
   *
   * @return true when the stacked bar should be shown
   */
  public boolean isDistributionAvailable() {
    return BANDS.containsKey(url) && distribution != null
        && distribution.optLong("total", 0) > 0;
  }

  /**
   * Distribution bands (name, percent, count, colour) for this metric.
   *
   * @return ordered list of bands, empty when unavailable
   */
  public List<Band> getBands() {
    String[][] labels = BANDS.get(url);
    if (labels == null || !isDistributionAvailable()) {
      return Collections.emptyList();
    }
    List<Band> bands = new ArrayList<>();
    for (int i = 0; i < labels.length && i < SCALE_KEYS.length; i++) {
      JSONObject scale = distribution.optJSONObject(SCALE_KEYS[i]);
      if (scale == null) {
        continue;
      }
      bands.add(new Band(labels[i][0], TableHtml.percent(scale.optDouble("ratio", 0)),
          String.format("%,d", scale.optLong("count")), labels[i][1]));
    }
    return bands;
  }

  /**
   * A single distribution band.
   */
  public static class Band {

    private final String name;
    private final int percent;
    private final String count;
    private final String color;

    Band(String name, int percent, String count, String color) {
      this.name = name;
      this.percent = percent;
      this.count = count;
      this.color = color;
    }

    public String getName() {
      return name;
    }

    public int getPercent() {
      return percent;
    }

    public String getCount() {
      return count;
    }

    public String getColor() {
      return color;
    }
  }
}
