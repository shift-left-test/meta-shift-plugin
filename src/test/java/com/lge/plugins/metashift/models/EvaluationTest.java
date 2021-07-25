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

  private void assertValues(ValueWithDifference<Double> object, double value, double difference) {
    assertEquals(value, object.getValue(), 0.01);
    assertEquals(difference, object.getDifference(), 0.01);
  }

  @Test
  public void testCreateObject() {
    Evaluation object = new PositiveEvaluation(true, 0, 0, 0.0);
    assertStatus(object, true, true);
    assertEquals(0, object.getDenominator());
    assertEquals(0, object.getNumerator());
    assertValues(object.getThreshold(), 0.00, 0.00);
  }

  @Test
  public void testCopyConstructor() {
    Evaluation object = new PositiveEvaluation(positiveQualified);
    assertStatus(object, true, true);
    assertValues(object.getRatio(), 0.66, 0.00);
    assertValues(object.getThreshold(), 0.50, 0.16);
  }

  @Test
  public void testDisabledPositiveQualified() {
    positiveQualified = new PositiveEvaluation(false, 3, 2, 0.50);
    assertStatus(positiveQualified, false, false);
    assertValues(positiveQualified.getRatio(), 0.66, 0.00);
    assertValues(positiveQualified.getThreshold(), 0.50, 0.16);
  }

  @Test
  public void testPositiveWithZeroDenominator() {
    Evaluation object = new PositiveEvaluation(true, 0, 1, 0.5);
    assertStatus(object, true, false);
    assertValues(object.getRatio(), 0.00, 0.00);
    assertValues(object.getThreshold(), 0.50, -0.50);
  }

  @Test
  public void testPositiveQualified() {
    assertStatus(positiveQualified, true, true);
    assertValues(positiveQualified.getRatio(), 0.66, 0.00);
    assertValues(positiveQualified.getThreshold(), 0.50, 0.16);
  }

  @Test
  public void testPositiveUnqualified() {
    assertStatus(positiveUnqualified, true, false);
    assertValues(positiveUnqualified.getRatio(), 0.33, 0.00);
    assertValues(positiveUnqualified.getThreshold(), 0.50, -0.17);
  }

  @Test
  public void testSetDifferenceOfPositiveQualified() {
    positiveQualified.setDifference(positiveUnqualified);
    assertEquals(0.33, positiveQualified.getRatio().getDifference(), 0.01);
  }

  @Test
  public void testDisabledNegativeQualified() {
    negativeQualified = new NegativeEvaluation(false, 3, 1, 0.50);
    assertStatus(negativeQualified, false, false);
    assertValues(negativeQualified.getRatio(), 0.33, 0.00);
    assertValues(negativeQualified.getThreshold(), 0.50, -0.17);
  }

  @Test
  public void testNegativeWithZeroDenominator() {
    Evaluation object = new NegativeEvaluation(true, 0, 1, 0.5);
    assertStatus(object, true, true);
    assertValues(object.getRatio(), 0.00, 0.00);
    assertValues(object.getThreshold(), 0.50, -0.50);
  }

  @Test
  public void testNegativeQualified() {
    assertStatus(negativeQualified, true, true);
    assertValues(negativeQualified.getRatio(), 0.33, 0.00);
    assertValues(negativeQualified.getThreshold(), 0.50, -0.17);
  }

  @Test
  public void testNegativeUnqualified() {
    assertStatus(negativeUnqualified, true, false);
    assertValues(negativeUnqualified.getRatio(), 0.66, 0.00);
    assertValues(negativeUnqualified.getThreshold(), 0.50, 0.16);
  }

  @Test
  public void testSetDifferenceOfNegativeQualified() {
    negativeQualified.setDifference(negativeUnqualified);
    assertEquals(-0.33, negativeQualified.getRatio().getDifference(), 0.01);
  }
}
