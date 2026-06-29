/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

/**
 * Server-side view model for a metric page's statistics/distribution header.
 *
 * <p>Replaces the former Lit {@code statistics-bar} and {@code distribution-bar}
 * web components: it formats the persisted evaluation/statistics/distribution
 * JSON into display values so the header can be rendered natively from Jelly.</p>
 *
 * @author Sung Gon Kim
 */
public class StatsHeaderView {

  /**
   * Distribution band labels and bootstrap colours per metric url.
   * Each entry maps the first/second/third/fourth distribution scales in order.
   */
  private static final Map<String, String[][]> BANDS = new HashMap<>();

  static {
    BANDS.put("unit_tests", new String[][]{
        {"Passed", "success"}, {"Failed", "warning"}, {"Error", "danger"}, {"Skipped", "secondary"}});
    BANDS.put("statement_coverage", new String[][]{
        {"Hits", "success"}, {"Misses", "secondary"}});
    BANDS.put("branch_coverage", new String[][]{
        {"Hits", "success"}, {"Misses", "secondary"}});
    BANDS.put("mutation_tests", new String[][]{
        {"Killed", "success"}, {"Survived", "warning"}, {"Skipped", "secondary"}});
  }

  private static final String[] SCALE_KEYS = {"first", "second", "third", "fourth"};

  private final JSONObject evaluation;
  private final JSONObject statistics;
  private final JSONObject distribution;
  private final String url;

  /**
   * Default constructor.
   *
   * @param evaluation   evaluation result JSON
   * @param statistics   statistics result JSON
   * @param distribution distribution result JSON
   * @param url          metric url, used to resolve band labels
   */
  public StatsHeaderView(JSONObject evaluation, JSONObject statistics, JSONObject distribution,
      String url) {
    this.evaluation = evaluation;
    this.statistics = statistics;
    this.distribution = distribution;
    this.url = url;
  }

  private static int percent(double ratio) {
    return (int) Math.floor(ratio * 100);
  }

  public boolean isAvailable() {
    return evaluation.optBoolean("available", false);
  }

  /**
   * Bootstrap contextual suffix for the value/threshold bar.
   *
   * @return "success", "danger" or "secondary"
   */
  public String getBarClass() {
    if (!isAvailable()) {
      return "secondary";
    }
    return evaluation.optBoolean("qualified", false) ? "success" : "danger";
  }

  public int getRatioPercent() {
    return isAvailable() ? percent(evaluation.optDouble("ratio", 0)) : 0;
  }

  public int getThresholdPercent() {
    return percent(evaluation.optDouble("threshold", 0));
  }

  /**
   * Whether min/avg/max statistics are available.
   *
   * @return true when the statistics line should be shown
   */
  public boolean isStatisticsAvailable() {
    return isAvailable() && statistics != null && !statistics.isEmpty();
  }

  public String getMinimum() {
    return percent(statistics.optDouble("min", 0)) + "%";
  }

  public String getAverage() {
    return percent(statistics.optDouble("average", 0)) + "%";
  }

  public String getMaximum() {
    return percent(statistics.optDouble("max", 0)) + "%";
  }

  /**
   * Whether a non-empty distribution is available.
   *
   * @return true when the distribution bar should be shown
   */
  public boolean isDistributionAvailable() {
    return distribution != null && distribution.optLong("total", 0) > 0;
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
      bands.add(new Band(labels[i][0], percent(scale.optDouble("ratio", 0)),
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
