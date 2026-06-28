/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the {@link MetricView} class.
 *
 * @author Sung Gon Kim
 */
public class MetricViewTest {

  private JSONObject evaluation(boolean available, boolean qualified, double ratio,
      double threshold, long numerator, long denominator) {
    JSONObject o = new JSONObject();
    o.put("available", available);
    o.put("qualified", qualified);
    o.put("ratio", ratio);
    o.put("threshold", threshold);
    o.put("numerator", numerator);
    o.put("denominator", denominator);
    return o;
  }

  private JSONObject statistics(double min, double average, double max) {
    JSONObject o = new JSONObject();
    o.put("min", min);
    o.put("average", average);
    o.put("max", max);
    return o;
  }

  @Test
  public void testQualifiedCardWithPositiveDelta() {
    MetricView view = new MetricView("Statement Coverage", "statement_coverage",
        evaluation(true, true, 0.875, 0.8, 1750, 2000), new JSONObject(), 0.021);
    assertTrue(view.isAvailable());
    assertTrue(view.isQualified());
    assertEquals("success", view.getBarClass());
    assertEquals("87%", view.getRatioText());
    assertEquals(87, view.getRatioPercent());
    assertEquals("▲ 2%", view.getDeltaText());
    assertEquals("1,750", view.getNumerator());
    assertEquals("2,000", view.getDenominator());
    assertEquals("80%", view.getThresholdText());
  }

  @Test
  public void testUnqualifiedCardWithNegativeDelta() {
    MetricView view = new MetricView("Branch Coverage", "branch_coverage",
        evaluation(true, false, 0.40, 0.5, 40, 100), new JSONObject(), -0.05);
    assertFalse(view.isQualified());
    assertEquals("danger", view.getBarClass());
    assertEquals("40%", view.getRatioText());
    assertEquals("▼ 5%", view.getDeltaText());
  }

  @Test
  public void testUnavailableCard() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        new JSONObject(), new JSONObject(), 0);
    assertFalse(view.isAvailable());
    assertEquals("secondary", view.getBarClass());
    assertEquals("N/A", view.getRatioText());
    assertEquals(0, view.getRatioPercent());
    assertEquals("", view.getDeltaText());
    assertFalse(view.isStatisticsAvailable());
  }

  @Test
  public void testStatisticsLine() {
    MetricView view = new MetricView("Mutation Tests", "mutation_tests",
        evaluation(true, true, 0.7, 0.6, 70, 100), statistics(0.5, 0.7, 0.9), 0);
    assertTrue(view.isStatisticsAvailable());
    assertEquals("50%", view.getMinimum());
    assertEquals("70%", view.getAverage());
    assertEquals("90%", view.getMaximum());
  }
}
