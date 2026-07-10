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

  private JSONObject scale(long count, double ratio) {
    JSONObject o = new JSONObject();
    o.put("count", count);
    o.put("ratio", ratio);
    return o;
  }

  private JSONObject distribution(long total, JSONObject... scales) {
    JSONObject o = new JSONObject();
    o.put("total", total);
    String[] keys = {"first", "second", "third", "fourth"};
    for (int i = 0; i < scales.length; i++) {
      o.put(keys[i], scales[i]);
    }
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
    assertEquals("▲ 2.1%", view.getDeltaText());
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
    assertEquals("▼ 5.0%", view.getDeltaText());
  }

  @Test
  public void testNewCardWithoutReference() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        evaluation(true, true, 0.8, 0.5, 8, 10), new JSONObject(), null);
    assertTrue(view.isNewMetric());
    assertEquals("", view.getDeltaText());
  }

  @Test
  public void testSmallNegativeDeltaIsNotHiddenByFlooring() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        evaluation(true, true, 0.8, 0.5, 8, 10), new JSONObject(), -0.004);
    assertEquals("▼ 0.4%", view.getDeltaText());
  }

  @Test
  public void testUnavailableCard() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        new JSONObject(), new JSONObject(), 0.0);
    assertFalse(view.isAvailable());
    assertEquals("secondary", view.getBarClass());
    assertEquals("N/A", view.getRatioText());
    assertEquals(0, view.getRatioPercent());
    assertEquals("", view.getDeltaText());
    assertFalse(view.isDistributionAvailable());
  }

  @Test
  public void testBandsForUnitTests() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        evaluation(true, false, 0.5, 0.95, 5, 10),
        distribution(10, scale(5, 0.5), scale(3, 0.3), scale(1, 0.1), scale(1, 0.1)), null);
    assertTrue(view.isDistributionAvailable());
    List<MetricView.Band> bands = view.getBands();
    assertEquals(4, bands.size());
    assertEquals("Passed", bands.get(0).getName());
    assertEquals("success", bands.get(0).getColor());
    assertEquals(50, bands.get(0).getPercent());
    assertEquals("5", bands.get(0).getCount());
    assertEquals("Failed", bands.get(1).getName());
    assertEquals("danger", bands.get(1).getColor());
  }

  @Test
  public void testMutationSurvivedBandUsesDanger() {
    MetricView view = new MetricView("Mutation Tests", "mutation_tests",
        evaluation(true, false, 0.7, 0.85, 7, 10),
        distribution(10, scale(7, 0.7), scale(2, 0.2), scale(1, 0.1)), null);
    List<MetricView.Band> bands = view.getBands();
    assertEquals("Survived", bands.get(1).getName());
    assertEquals("danger", bands.get(1).getColor());
  }

  @Test
  public void testBandsEmptyWhenDistributionUnavailable() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        evaluation(true, true, 0.8, 0.5, 8, 10), new JSONObject(), null);
    assertFalse(view.isDistributionAvailable());
    assertTrue(view.getBands().isEmpty());
  }

  @Test
  public void testDistributionUnavailableForUnknownUrl() {
    MetricView view = new MetricView("Custom", "custom_metric",
        evaluation(true, true, 0.8, 0.5, 8, 10), distribution(10, scale(8, 0.8)), null);
    assertFalse(view.isDistributionAvailable());
    assertTrue(view.getBands().isEmpty());
  }

  @Test
  public void testBandsSkipMissingScales() {
    MetricView view = new MetricView("Unit Tests", "unit_tests",
        evaluation(true, false, 0.5, 0.95, 5, 10),
        distribution(10, scale(5, 0.5)), null);
    List<MetricView.Band> bands = view.getBands();
    assertEquals(1, bands.size());
    assertEquals("Passed", bands.get(0).getName());
  }
}
