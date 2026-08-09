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
import java.util.function.Supplier;
import net.sf.json.JSONArray;
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
  private final Supplier<JSONObject> distributionSource;
  private final Double delta;
  private JSONObject distribution;

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
    this(name, url, evaluation, () -> distribution, delta);
  }

  /**
   * Constructor taking the distribution lazily.
   *
   * <p>Each distribution is a separate read from the build's store, and the recent-builds table
   * on the trend page renders twenty builds of four metrics from {@code barClass} and
   * {@code ratioText} alone — both evaluation-only. Deferring the read halves what that page
   * costs.
   *
   * @param name         metric display name
   * @param url          metric url segment, used as the section anchor and band key
   * @param evaluation   evaluation result JSON
   * @param distribution supplies the distribution result JSON on first use
   * @param delta        build-over-build ratio delta, null when there is no reference build
   */
  public MetricView(String name, String url, JSONObject evaluation,
      Supplier<JSONObject> distribution, Double delta) {
    this.name = name;
    this.url = url;
    this.evaluation = evaluation;
    this.distributionSource = distribution;
    this.delta = delta;
  }

  private JSONObject distribution() {
    if (distribution == null) {
      distribution = distributionSource.get();
    }
    return distribution;
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
    // A build record that only partly deserialized hands back a report without groups
    // (see Report#get); render the card as unavailable rather than failing the page.
    if (group == null) {
      return new MetricView(name, url, new JSONObject(), new JSONObject(), delta);
    }
    return new MetricView(name, url, group.getEvaluation(), group::getDistribution, delta);
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
   * Explains why {@link #getRatioText()} shows "N/A": no report of this metric was found.
   * Used on both the build-wide and the per-recipe cards, so the text names only the metric.
   * Empty when the metric is available.
   *
   * @return reason text, empty when available
   */
  public String getUnavailableReasonText() {
    return isAvailable() ? "" : "No " + name + " report found.";
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
    JSONObject values = distribution();
    return BANDS.containsKey(url) && values != null && values.optLong("total", 0) > 0;
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
      JSONObject scale = distribution().optJSONObject(SCALE_KEYS[i]);
      if (scale == null) {
        continue;
      }
      bands.add(new Band(labels[i][0], TableHtml.percent(scale.optDouble("ratio", 0)),
          scale.optLong("count"), labels[i][1]));
    }
    return bands;
  }

  /**
   * Distribution bands as a JSON array for the donut chart, e.g.
   * {@code [{"name":"Passed","value":8}, ...]}. Empty array when unavailable.
   *
   * @return JSON array string
   */
  public String getDistributionJson() {
    JSONArray array = new JSONArray();
    for (Band band : getBands()) {
      JSONObject entry = new JSONObject();
      entry.put("name", band.getName());
      entry.put("value", band.getValue());
      array.add(entry);
    }
    return array.toString();
  }

  /**
   * A single distribution band.
   */
  public static class Band {

    private final String name;
    private final int percent;
    private final long value;
    private final String color;

    Band(String name, int percent, long value, String color) {
      this.name = name;
      this.percent = percent;
      this.value = value;
      this.color = color;
    }

    public String getName() {
      return name;
    }

    public int getPercent() {
      return percent;
    }

    /**
     * Raw count of this band, for the donut chart JSON.
     *
     * @return raw count
     */
    public long getValue() {
      return value;
    }

    /**
     * Formatted count for display, e.g. {@code "1,234"}.
     *
     * @return formatted count
     */
    public String getCount() {
      return String.format("%,d", value);
    }

    public String getColor() {
      return color;
    }
  }
}
