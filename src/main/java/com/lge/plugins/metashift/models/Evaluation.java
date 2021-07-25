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

import java.io.Serializable;

/**
 * Evaluation class.
 *
 * @author Sung Gon Kim
 */
public abstract class Evaluation implements Serializable {

  private static final long serialVersionUID = 6414517999564046375L;

  private final boolean available;
  private final long denominator;
  private final long numerator;
  private ValueWithDifference<Double> ratio;
  private final ValueWithDifference<Double> threshold;

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   */
  public Evaluation(boolean available, long denominator, long numerator, double threshold) {
    this.available = available;
    this.denominator = denominator;
    this.numerator = numerator;
    double ratio = (double) denominator > 0.0 ? (double) numerator / (double) denominator : 0.0;
    this.ratio = new ValueWithDifference<>(ratio, 0.0);
    this.threshold = new ValueWithDifference<>(threshold, ratio - threshold);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Evaluation(Evaluation other) {
    this.available = other.available;
    this.denominator = other.denominator;
    this.numerator = other.numerator;
    this.ratio = new ValueWithDifference<>(other.getRatio());
    this.threshold = new ValueWithDifference<>(other.getThreshold());
  }

  /**
   * Returns whether the metric is available.
   *
   * @return true if the metric is available, false otherwise
   */
  public boolean isAvailable() {
    return available;
  }

  /**
   * Returns whether the evaluation of the metric meets the criteria.
   *
   * @return true if the metric meets the criteria, false otherwise
   */
  public abstract boolean isQualified();

  /**
   * Returns the denominator value.
   *
   * @return denominator
   */
  public long getDenominator() {
    return denominator;
  }

  /**
   * Returns the numerator value.
   *
   * @return numerator
   */
  public long getNumerator() {
    return numerator;
  }

  /**
   * Returns the ratio and difference values.
   *
   * @return ratio values
   */
  public ValueWithDifference<Double> getRatio() {
    return ratio;
  }

  /**
   * Returns the threshold and difference values.
   *
   * @return threshold values
   */
  public ValueWithDifference<Double> getThreshold() {
    return threshold;
  }

  /**
   * Sets the difference value of the ratio.
   *
   * @param other object
   */
  public void setDifference(Evaluation other) {
    double ratio = getRatio().getValue();
    double difference = getRatio().getValue() - other.getRatio().getValue();
    this.ratio = new ValueWithDifference<>(ratio, difference);
  }
}
