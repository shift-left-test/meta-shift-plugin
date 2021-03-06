/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the DataSummary class.
 *
 * @author Sung Gon Kim
 */
public class DataSummaryTest {

  private static final String RECIPE = "A-1.0.0-r0";
  private static final LinesOfCode LINES_OF_CODE = new LinesOfCode(1, 2, 3, 4, 5);
  private static final Distribution DISTRIBUTION = new Distribution(1, 2);
  private static final Evaluation EVALUATION = new PositiveEvaluation(true, 1, 2, 0.5);
  private static final DataSummary origin = new DataSummary(RECIPE, LINES_OF_CODE, DISTRIBUTION,
      EVALUATION);
  private static final DataSummary same = new DataSummary(RECIPE, LINES_OF_CODE, DISTRIBUTION,
      EVALUATION);

  private void assertLinesOfCode(DataSummary o, long linesOfCode) {
    assertEquals(linesOfCode, o.getLinesOfCode());
  }

  private void assertDistribution(DataSummary o, long first, long second, long third, long fourth) {
    long total = first + second + third + fourth;
    assertEquals(total, o.getTotal());
    assertEquals(first, o.getFirst());
    assertEquals(second, o.getSecond());
    assertEquals(third, o.getThird());
    assertEquals(fourth, o.getFourth());
  }

  private void assertEvaluation(DataSummary o, double ratio, boolean qualified) {
    assertEquals(ratio, o.getRatio(), 0.01);
    assertEquals(qualified, o.isQualified());
  }

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertLinesOfCode(origin, 1);
    assertDistribution(origin, 1, 2, 0, 0);
    assertEvaluation(origin, 2.0, true);
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new DataSummary("B-1.0.0-r0", LINES_OF_CODE, DISTRIBUTION, EVALUATION));
    assertEquals(origin,
        new DataSummary(RECIPE, new LinesOfCode(4, 3, 2, 1, 0), DISTRIBUTION, EVALUATION));
    assertEquals(origin,
        new DataSummary(RECIPE, LINES_OF_CODE, new Distribution(2, 1), EVALUATION));
    assertEquals(origin,
        new DataSummary(RECIPE, LINES_OF_CODE, DISTRIBUTION,
            new PositiveEvaluation(false, 2, 1, 0.5)));
  }

  @Test
  public void testHashCode() {
    assertHashNotEquals(origin, new Object());
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin,
        new DataSummary("B-1.0.0-r0", LINES_OF_CODE, DISTRIBUTION, EVALUATION));
    assertHashEquals(origin,
        new DataSummary(RECIPE, new LinesOfCode(4, 3, 2, 1, 0), DISTRIBUTION, EVALUATION));
    assertHashEquals(origin,
        new DataSummary(RECIPE, LINES_OF_CODE, new Distribution(2, 1), EVALUATION));
    assertHashEquals(origin,
        new DataSummary(RECIPE, LINES_OF_CODE, DISTRIBUTION,
            new PositiveEvaluation(false, 2, 1, 0.5)));
  }
}
