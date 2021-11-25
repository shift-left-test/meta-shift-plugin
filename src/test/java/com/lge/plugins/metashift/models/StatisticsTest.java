/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.util.DoubleSummaryStatistics;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Statistics class.
 *
 * @author Sung Gon Kim
 */
public class StatisticsTest {

  Statistics object;

  @Before
  public void setUp() {
    object = new Statistics(of(1.0, 2.0, 3.0, 4.0));
  }

  private DoubleSummaryStatistics of(double... values) {
    DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
    for (double value : values) {
      statistics.accept(value);
    }
    return statistics;
  }

  private void assertValues(Statistics o, double min, double average, double max) {
    assertEquals(min, o.getMin(), 0.01);
    assertEquals(average, o.getAverage(), 0.01);
    assertEquals(max, o.getMax(), 0.01);
  }

  @Test
  public void testCreateObject() {
    assertValues(object, 1.0, 2.5, 4.0);
  }

  @Test
  public void testCopyConstructor() {
    Statistics copied = new Statistics(object);
    assertValues(copied, 1.0, 2.5, 4.0);
  }

  @Test
  public void testCreateWithEmptySummaryStatistics() {
    object = new Statistics(new DoubleSummaryStatistics());
    assertValues(object, 0.0, 0.0, 0.0);
  }
}
