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
        new LinesOfCode(1, 2, 3, 4, 5),
        newEvaluation(false, 0, 0, 0.0),
        newEvaluation(true, 10, 1, 0.5),
        newEvaluation(true, 10, 2, 0.5),
        newEvaluation(true, 10, 3, 0.5),
        newEvaluation(true, 10, 4, 0.5),
        newEvaluation(true, 10, 5, 0.5),
        newEvaluation(true, 10, 6, 0.5),
        newEvaluation(true, 10, 7, 0.5),
        newEvaluation(true, 10, 8, 0.5),
        newEvaluation(true, 10, 9, 0.5),
        newEvaluation(true, 10, 10, 0.5)
    );
    assertEquals(1, summary.getLinesOfCode());
    assertEquals(0.0, summary.getPremirrorCache().getRatio(), 0.01);
    assertEquals(0.1, summary.getSharedStateCache().getRatio(), 0.01);
    assertEquals(0.2, summary.getRecipeViolations().getRatio(), 0.01);
    assertEquals(0.3, summary.getComments().getRatio(), 0.01);
    assertEquals(0.4, summary.getCodeViolations().getRatio(), 0.01);
    assertEquals(0.5, summary.getComplexity().getRatio(), 0.01);
    assertEquals(0.6, summary.getDuplications().getRatio(), 0.01);
    assertEquals(0.7, summary.getUnitTests().getRatio(), 0.01);
    assertEquals(0.8, summary.getStatementCoverage().getRatio(), 0.01);
    assertEquals(0.9, summary.getBranchCoverage().getRatio(), 0.01);
    assertEquals(1.0, summary.getMutationTests().getRatio(), 0.01);
  }

  @Test
  public void testEquality() {
    LinesOfCode LOC = new LinesOfCode(1, 2, 3, 4, 5);
    LinesOfCode LOC2 = new LinesOfCode(5, 4, 3, 2, 1);
    Evaluation E = newEvaluation(true, 10, 1, 0.5);
    Evaluation X = newEvaluation(false, 1, 2, 0.5);
    EvaluationSummary origin = new EvaluationSummary(RECIPE, LOC, E, E, E, E, E, E, E, E, E, E, E);
    EvaluationSummary same = new EvaluationSummary(RECIPE, LOC, E, E, E, E, E, E, E, E, E, E, E);

    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new EvaluationSummary("B-X-X", LOC, E, E, E, E, E, E, E, E, E, E, E));
    assertEquals(origin, new EvaluationSummary(RECIPE, LOC2, E, E, E, E, E, E, E, E, E, E, E));
    assertEquals(origin, new EvaluationSummary(RECIPE, LOC, X, X, X, X, X, X, X, X, X, X, X));
  }

  @Test
  public void testHashCode() {
    LinesOfCode LOC = new LinesOfCode(1, 2, 3, 4, 5);
    LinesOfCode LOC2 = new LinesOfCode(5, 4, 3, 2, 1);
    Evaluation E = newEvaluation(true, 10, 1, 0.5);
    Evaluation X = newEvaluation(false, 1, 2, 0.5);
    EvaluationSummary origin = new EvaluationSummary(RECIPE, LOC, E, E, E, E, E, E, E, E, E, E, E);
    EvaluationSummary same = new EvaluationSummary(RECIPE, LOC, E, E, E, E, E, E, E, E, E, E, E);

    assertHashNotEquals(origin, new Object());
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin,
        new EvaluationSummary("B-X-X", LOC, E, E, E, E, E, E, E, E, E, E, E));
    assertHashEquals(origin, new EvaluationSummary(RECIPE, LOC2, E, E, E, E, E, E, E, E, E, E, E));
    assertHashEquals(origin, new EvaluationSummary(RECIPE, LOC, X, X, X, X, X, X, X, X, X, X, X));
  }
}
