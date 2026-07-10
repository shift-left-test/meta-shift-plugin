/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Evaluation class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationTest {

  private Evaluation positiveQualified;
  private Evaluation positiveUnqualified;

  @Before
  public void setUp() {
    positiveQualified = new PositiveEvaluation(true, 3, 2, 0.50);
    positiveUnqualified = new PositiveEvaluation(true, 3, 1, 0.50);
  }

  private void assertStatus(Evaluation object, boolean available, boolean qualified) {
    assertEquals(available, object.isAvailable());
    assertEquals(qualified, object.isQualified());
  }

  @Test
  public void testCreateObject() {
    Evaluation object = new PositiveEvaluation(true, 0, 0, 0.0, 10);
    assertStatus(object, true, true);
    assertEquals(0, object.getDenominator());
    assertEquals(0, object.getNumerator());
    assertEquals(0.0, object.getThreshold(), 0.01);
    assertEquals(10, object.getTolerance());
  }

  @Test
  public void testCopyConstructorForPositiveEvaluation() {
    Evaluation object = new PositiveEvaluation(positiveQualified);
    assertStatus(object, true, true);
    assertEquals(0.66, object.getRatio(), 0.01);
    assertEquals(0.50, object.getThreshold(), 0.01);
    assertEquals(0, object.getTolerance());
  }

  @Test
  public void testDisabledPositiveQualified() {
    positiveQualified = new PositiveEvaluation(false, 3, 2, 0.50);
    assertStatus(positiveQualified, false, false);
    assertEquals(0.66, positiveQualified.getRatio(), 0.01);
    assertEquals(0.50, positiveQualified.getThreshold(), 0.01);
  }

  @Test
  public void testPositiveWithZeroDenominator() {
    Evaluation object = new PositiveEvaluation(true, 0, 1, 0.5);
    assertStatus(object, true, false);
    assertEquals(0.0, object.getRatio(), 0.01);
    assertEquals(0.5, object.getThreshold(), 0.01);
  }

  @Test
  public void testPositiveQualified() {
    assertStatus(positiveQualified, true, true);
    assertEquals(0.66, positiveQualified.getRatio(), 0.01);
    assertEquals(0.50, positiveQualified.getThreshold(), 0.01);
  }

  @Test
  public void testPositiveUnqualified() {
    assertStatus(positiveUnqualified, true, false);
    assertEquals(0.33, positiveUnqualified.getRatio(), 0.01);
    assertEquals(0.50, positiveUnqualified.getThreshold(), 0.01);
  }
}
