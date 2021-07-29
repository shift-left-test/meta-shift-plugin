/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.util.DoubleSummaryStatistics;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MetricDataSummary class.
 *
 * @author Sung Gon Kim
 */
public class MetricDataSummaryTest {

  private MetricDataSummary summary;

  @Before
  public void setUp() {
    summary = new MetricDataSummary();
  }

  private DoubleSummaryStatistics of(double... values) {
    DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
    for (double value : values) {
      statistics.accept(value);
    }
    return statistics;
  }

  private void assertEvaluation(boolean available, long denominator, long numerator,
      double threshold) {
    assertEquals(available, summary.getEvaluation().isAvailable());
    assertEquals(denominator, summary.getEvaluation().getDenominator());
    assertEquals(numerator, summary.getEvaluation().getNumerator());
    assertEquals(threshold, summary.getEvaluation().getThreshold(), 0.01);
  }

  private void assertStatistics(double min, double average, double max, double threshold) {
    assertEquals(min, summary.getStatistics().getMin(), 0.01);
    assertEquals(average, summary.getStatistics().getAverage(), 0.01);
    assertEquals(max, summary.getStatistics().getMax(), 0.01);
    assertEquals(threshold, summary.getStatistics().getThreshold(), 0.01);
  }

  private void assertDistribution(long first, long second) {
    assertEquals(first, summary.getDistribution().getFirst().getCount());
    assertEquals(second, summary.getDistribution().getSecond().getCount());
  }

  @Test
  public void testInitialState() {
    assertEvaluation(false, 0, 0, 0.0);
    assertStatistics(0.0, 0.0, 0.0, 0.0);
    assertDistribution(0, 0);
  }

  @Test
  public void testCreateObject() {
    Evaluation evaluation = new PositiveEvaluation(true, 10, 5, 0.5);
    Statistics statistics = new Statistics(of(1.0, 2.0, 3.0, 4.0), 0.5);
    Distribution distribution = new Distribution(10, 5);
    summary = new MetricDataSummary(evaluation, statistics, distribution);
    assertEvaluation(true, 10, 5, 0.5);
    assertStatistics(1.0, 2.5, 4.0, 0.5);
    assertDistribution(10, 5);
  }
}
