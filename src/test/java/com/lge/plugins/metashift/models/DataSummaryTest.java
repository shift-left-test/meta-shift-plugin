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

import org.junit.Test;

/**
 * Unit tests for the DataSummary class.
 *
 * @author Sung Gon Kim
 */
public class DataSummaryTest {

  private static final String RECIPE = "A-1.0.0-r0";

  private void assertLinesOfCode(DataSummary o, long linesOfCode) {
    assertEquals(linesOfCode, o.getLinesOfCode().getLines());
  }

  private void assertDistribution(DataSummary o, long first, long second, long third, long fourth) {
    long total = first + second + third + fourth;
    assertEquals(total, o.getDistribution().getTotal());
    assertEquals(first, o.getDistribution().getFirst().getCount());
    assertEquals(second, o.getDistribution().getSecond().getCount());
    assertEquals(third, o.getDistribution().getThird().getCount());
    assertEquals(fourth, o.getDistribution().getFourth().getCount());
  }

  private void assertEvaluation(DataSummary o, double ratio, boolean qualified) {
    assertEquals(ratio, o.getEvaluation().getRatio(), 0.01);
    assertEquals(qualified, o.getEvaluation().isQualified());
  }

  @Test
  public void testCreateObject() {
    LinesOfCode linesOfCode = new LinesOfCode(1, 2, 3, 4, 5);
    Distribution distribution = new Distribution(1, 2);
    Evaluation evaluation = new PositiveEvaluation(true, 1, 2, 0.5);
    DataSummary summary = new DataSummary(RECIPE, linesOfCode, distribution, evaluation);
    assertLinesOfCode(summary, 1);
    assertDistribution(summary, 1, 2, 0, 0);
    assertEvaluation(summary, 2.0, true);
  }
}
