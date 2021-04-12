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

package com.lge.plugins.metashift.metrics2;

/**
 * Counter class.
 *
 * @author Sung Gon Kim
 */
public class Counter {

  /**
   * Represents the denominator.
   */
  private long denominator;

  /**
   * Represents the numerator.
   */
  private long numerator;

  /**
   * Default constructor.
   */
  public Counter() {
    this(0, 0);
  }

  /**
   * Default constructor.
   *
   * @param denominator value
   * @param numerator   value
   */
  public Counter(final long denominator, final long numerator) {
    this.denominator = denominator;
    this.numerator = numerator;
  }

  /**
   * Returns the denominator of the metric.
   *
   * @return denominator
   */
  public long getDenominator() {
    return denominator;
  }

  /**
   * Sets the denominator value.
   *
   * @param denominator value
   */
  public void setDenominator(final long denominator) {
    this.denominator = denominator;
  }

  /**
   * Returns the numerator of the metric.
   *
   * @return numerator
   */
  public long getNumerator() {
    return numerator;
  }

  /**
   * Sets the numerator value.
   *
   * @param numerator value
   */
  public void setNumerator(final long numerator) {
    this.numerator = numerator;
  }

  /**
   * Returns the calculated ratio of the metric or zero if the denominator is zero.
   *
   * @return ratio
   */
  public double getRatio() {
    long denominator = getDenominator();
    long numerator = getNumerator();
    return denominator > 0 ? (double) numerator / (double) denominator : 0.0;
  }
}
