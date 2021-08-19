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
