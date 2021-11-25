/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the SummaryStatistics class.
 *
 * @author Sung Gon Kim
 */
public class SummaryStatisticsTest {

  private SummaryStatistics stats;

  @Before
  public void setUp() {
    List<String> strings = Arrays.asList("1", "12", "123", "1234");
    DoubleSummaryStatistics source = strings.stream()
        .collect(Collectors.summarizingDouble(String::length));
    stats = new SummaryStatistics(source);
  }

  @Test
  public void testInitialState() {
    stats = new SummaryStatistics();
    assertEquals(0, stats.getCount());
    assertEquals(0.0, stats.getSum(), 0.1);
    assertEquals(0.0, stats.getMin(), 0.1);
    assertEquals(0.0, stats.getMax(), 0.1);
    assertEquals(0.0, stats.getAverage(), 0.1);
  }

  @Test
  public void testCreateWithVoidDoubleSummaryStatistics() {
    stats = new SummaryStatistics(new DoubleSummaryStatistics());
    assertEquals(0, stats.getCount());
    assertEquals(0.0, stats.getSum(), 0.1);
    assertEquals(0.0, stats.getMin(), 0.1);
    assertEquals(0.0, stats.getMax(), 0.1);
    assertEquals(0.0, stats.getAverage(), 0.1);
  }

  @Test
  public void testCreateWithDoubleSummaryStatistics() {
    assertEquals(4, stats.getCount());
    assertEquals(10.0, stats.getSum(), 0.1);
    assertEquals(1.0, stats.getMin(), 0.1);
    assertEquals(4.0, stats.getMax(), 0.1);
    assertEquals(2.5, stats.getAverage(), 0.1);
  }
}
