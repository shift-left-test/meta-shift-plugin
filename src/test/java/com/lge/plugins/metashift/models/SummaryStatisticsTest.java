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
import net.sf.json.JSONObject;
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
    stats = new SummaryStatistics(of(1.0, 2.0, 3.0, 4.0));
  }

  private DoubleSummaryStatistics of(double... values) {
    DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
    for (double value : values) {
      statistics.accept(value);
    }
    return statistics;
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

  @Test
  public void testJsonObjectOfInitialData() {
    stats = new SummaryStatistics();
    JSONObject object = stats.toJsonObject();
    assertEquals(0, object.getLong("count"));
    assertEquals(0.0, object.getDouble("sum"), 0.1);
    assertEquals(0.0, object.getDouble("min"), 0.1);
    assertEquals(0.0, object.getDouble("max"), 0.1);
    assertEquals(0.0, object.getDouble("average"), 0.1);
  }

  @Test
  public void testJsonObjectOfVoidData() {
    stats = new SummaryStatistics(new DoubleSummaryStatistics());
    JSONObject object = stats.toJsonObject();
    assertEquals(0, object.getLong("count"));
    assertEquals(0.0, object.getDouble("sum"), 0.1);
    assertEquals(0.0, object.getDouble("min"), 0.1);
    assertEquals(0.0, object.getDouble("max"), 0.1);
    assertEquals(0.0, object.getDouble("average"), 0.1);
  }

  @Test
  public void testJsonObjectOfNormalData() {
    JSONObject object = stats.toJsonObject();
    assertEquals(4, object.getLong("count"));
    assertEquals(10.0, object.getDouble("sum"), 0.1);
    assertEquals(1.0, object.getDouble("min"), 0.1);
    assertEquals(4.0, object.getDouble("max"), 0.1);
    assertEquals(2.5, object.getDouble("average"), 0.1);
  }
}
