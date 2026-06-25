/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the EvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummaryTest {

  private static final String RECIPE = "A-1.0.0-r0";

  private Evaluation newEvaluation(boolean available, long denominator, long numerator,
      double threshold) {
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testCreateObject() {
    EvaluationSummary summary = new EvaluationSummary(
        RECIPE,
        newEvaluation(true, 10, 7, 0.5),
        newEvaluation(true, 10, 8, 0.5),
        newEvaluation(true, 10, 9, 0.5),
        newEvaluation(true, 10, 10, 0.5)
    );
    assertEquals(0.7, summary.getUnitTests().getRatio(), 0.01);
    assertEquals(0.8, summary.getStatementCoverage().getRatio(), 0.01);
    assertEquals(0.9, summary.getBranchCoverage().getRatio(), 0.01);
    assertEquals(1.0, summary.getMutationTests().getRatio(), 0.01);
  }

  @Test
  public void testEquality() {
    Evaluation E = newEvaluation(true, 10, 1, 0.5);
    Evaluation X = newEvaluation(false, 1, 2, 0.5);
    EvaluationSummary origin = new EvaluationSummary(RECIPE, E, E, E, E);
    EvaluationSummary same = new EvaluationSummary(RECIPE, E, E, E, E);

    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new EvaluationSummary("B-X-X", E, E, E, E));
    assertEquals(origin, new EvaluationSummary(RECIPE, X, X, X, X));
  }

  @Test
  public void testHashCode() {
    Evaluation E = newEvaluation(true, 10, 1, 0.5);
    Evaluation X = newEvaluation(false, 1, 2, 0.5);
    EvaluationSummary origin = new EvaluationSummary(RECIPE, E, E, E, E);
    EvaluationSummary same = new EvaluationSummary(RECIPE, E, E, E, E);

    assertHashNotEquals(origin, new Object());
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new EvaluationSummary("B-X-X", E, E, E, E));
    assertHashEquals(origin, new EvaluationSummary(RECIPE, X, X, X, X));
  }
}
