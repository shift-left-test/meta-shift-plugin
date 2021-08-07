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
  private Evaluation negativeQualified;
  private Evaluation negativeUnqualified;

  @Before
  public void setUp() {
    positiveQualified = new PositiveEvaluation(true, 3, 2, 0.50);
    positiveUnqualified = new PositiveEvaluation(true, 3, 1, 0.50);
    negativeQualified = new NegativeEvaluation(true, 3, 1, 0.50);
    negativeUnqualified = new NegativeEvaluation(true, 3, 2, 0.50);
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
  public void testCopyConstructor() {
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

  @Test
  public void testDisabledNegativeQualified() {
    negativeQualified = new NegativeEvaluation(false, 3, 1, 0.50);
    assertStatus(negativeQualified, false, false);
    assertEquals(0.33, negativeQualified.getRatio(), 0.01);
    assertEquals(0.50, negativeQualified.getThreshold(), 0.01);
  }

  @Test
  public void testNegativeWithZeroDenominator() {
    Evaluation object = new NegativeEvaluation(true, 0, 1, 0.5);
    assertStatus(object, true, true);
    assertEquals(0.00, object.getRatio(), 0.01);
    assertEquals(0.50, object.getThreshold(), 0.01);
  }

  @Test
  public void testNegativeQualified() {
    assertStatus(negativeQualified, true, true);
    assertEquals(0.33, negativeQualified.getRatio(), 0.01);
    assertEquals(0.50, negativeQualified.getThreshold(), 0.01);
  }

  @Test
  public void testNegativeUnqualified() {
    assertStatus(negativeUnqualified, true, false);
    assertEquals(0.66, negativeUnqualified.getRatio(), 0.01);
    assertEquals(0.50, negativeUnqualified.getThreshold(), 0.01);
  }
}
