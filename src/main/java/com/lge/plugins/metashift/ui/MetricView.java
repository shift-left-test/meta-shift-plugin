/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import com.lge.plugins.metashift.builders.Group;
import com.lge.plugins.metashift.builders.Report;
import com.lge.plugins.metashift.ui.tables.TableHtml;
import java.util.Arrays;
import java.util.List;
import net.sf.json.JSONObject;

/**
 * Server-side view model for a single metric summary card.
 *
 * <p>Replaces the former Lit {@code metrics-simple-view} web component by
 * formatting the persisted evaluation/statistics JSON into display strings so
 * the card can be rendered natively from Jelly.</p>
 *
 * @author Sung Gon Kim
 */
public class MetricView {

  private final String name;
  private final String url;
  private final JSONObject evaluation;
  private final JSONObject statistics;
  private final double delta;

  /**
   * Default constructor.
   *
   * @param name       metric display name
   * @param url        relative url to the metric detail page
   * @param evaluation evaluation result JSON
   * @param statistics statistics result JSON
   * @param delta      build-over-build ratio delta
   */
  public MetricView(String name, String url, JSONObject evaluation, JSONObject statistics,
      double delta) {
    this.name = name;
    this.url = url;
    this.evaluation = evaluation;
    this.statistics = statistics;
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
  public static List<MetricView> cardsFor(Report<? extends Group> report, double testDelta,
      double statementDelta, double branchDelta, double mutationDelta) {
    return Arrays.asList(
        of("Unit Tests", "unit_tests", report.getUnitTests(), testDelta),
        of("Statement Coverage", "statement_coverage", report.getStatementCoverage(),
            statementDelta),
        of("Branch Coverage", "branch_coverage", report.getBranchCoverage(), branchDelta),
        of("Mutation Tests", "mutation_tests", report.getMutationTests(), mutationDelta));
  }

  private static MetricView of(String name, String url, Group group, double delta) {
    return new MetricView(name, url, group.getEvaluation(), group.getStatistics(), delta);
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public boolean isAvailable() {
    return evaluation.optBoolean("available", false);
  }

  public boolean isQualified() {
    return evaluation.optBoolean("qualified", false);
  }

  /**
   * Bootstrap contextual suffix used for the badge and progress bar colour.
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
   * Integer ratio percent (0-100) for the progress bar width.
   *
   * @return ratio percent
   */
  public int getRatioPercent() {
    return isAvailable() ? TableHtml.percent(evaluation.optDouble("ratio", 0)) : 0;
  }

  /**
   * Build-over-build delta as an arrow and percent, empty when zero/unavailable.
   *
   * @return delta text
   */
  public String getDeltaText() {
    if (!isAvailable() || delta == 0) {
      return "";
    }
    String arrow = delta > 0 ? "▲" : "▼";
    return arrow + " " + (int) Math.floor(Math.abs(delta * 100)) + "%";
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
   * Whether min/avg/max statistics are available for this metric.
   *
   * @return true when the statistics line should be shown
   */
  public boolean isStatisticsAvailable() {
    return isAvailable() && statistics != null && !statistics.isEmpty();
  }

  public String getMinimum() {
    return TableHtml.percent(statistics.optDouble("min", 0)) + "%";
  }

  public String getAverage() {
    return TableHtml.percent(statistics.optDouble("average", 0)) + "%";
  }

  public String getMaximum() {
    return TableHtml.percent(statistics.optDouble("max", 0)) + "%";
  }
}
