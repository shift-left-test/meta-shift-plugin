/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the {@link StatsHeaderView} class.
 *
 * @author Sung Gon Kim
 */
public class StatsHeaderViewTest {

  private JSONObject evaluation(boolean available, boolean qualified, double ratio,
      double threshold) {
    JSONObject o = new JSONObject();
    o.put("available", available);
    o.put("qualified", qualified);
    o.put("ratio", ratio);
    o.put("threshold", threshold);
    return o;
  }

  private JSONObject statistics(double min, double average, double max) {
    JSONObject o = new JSONObject();
    o.put("min", min);
    o.put("average", average);
    o.put("max", max);
    return o;
  }

  private JSONObject scale(long count, double ratio) {
    JSONObject o = new JSONObject();
    o.put("count", count);
    o.put("ratio", ratio);
    return o;
  }

  private JSONObject distribution(long total, JSONObject... scales) {
    JSONObject o = new JSONObject();
    String[] keys = {"first", "second", "third", "fourth"};
    for (int i = 0; i < scales.length; i++) {
      o.put(keys[i], scales[i]);
    }
    o.put("total", total);
    return o;
  }

  @Test
  public void testCoverageHeaderWithTwoBands() {
    StatsHeaderView view = new StatsHeaderView(
        evaluation(true, true, 0.8, 0.7), statistics(0.5, 0.7, 0.9),
        distribution(200, scale(160, 0.8), scale(40, 0.2)), "statement_coverage");
    assertTrue(view.isAvailable());
    assertEquals("success", view.getBarClass());
    assertEquals(80, view.getRatioPercent());
    assertEquals(70, view.getThresholdPercent());
    assertTrue(view.isStatisticsAvailable());
    assertEquals("50%", view.getMinimum());
    assertEquals("90%", view.getMaximum());
    assertTrue(view.isDistributionAvailable());
    List<StatsHeaderView.Band> bands = view.getBands();
    assertEquals(2, bands.size());
    assertEquals("Hits", bands.get(0).getName());
    assertEquals(80, bands.get(0).getPercent());
    assertEquals("160", bands.get(0).getCount());
    assertEquals("success", bands.get(0).getColor());
    assertEquals("Misses", bands.get(1).getName());
    assertEquals("secondary", bands.get(1).getColor());
  }

  @Test
  public void testMutationHeaderWithThreeBands() {
    StatsHeaderView view = new StatsHeaderView(
        evaluation(true, false, 0.5, 0.6), statistics(0.3, 0.5, 0.7),
        distribution(100, scale(50, 0.5), scale(30, 0.3), scale(20, 0.2)), "mutation_tests");
    assertEquals("danger", view.getBarClass());
    List<StatsHeaderView.Band> bands = view.getBands();
    assertEquals(3, bands.size());
    assertEquals("Killed", bands.get(0).getName());
    assertEquals("Survived", bands.get(1).getName());
    assertEquals("warning", bands.get(1).getColor());
    assertEquals("Skipped", bands.get(2).getName());
  }

  @Test
  public void testUnavailableHeader() {
    StatsHeaderView view = new StatsHeaderView(
        new JSONObject(), new JSONObject(), new JSONObject(), "unit_tests");
    assertFalse(view.isAvailable());
    assertEquals("secondary", view.getBarClass());
    assertFalse(view.isStatisticsAvailable());
    assertFalse(view.isDistributionAvailable());
    assertTrue(view.getBands().isEmpty());
  }
}
