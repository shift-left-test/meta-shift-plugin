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
