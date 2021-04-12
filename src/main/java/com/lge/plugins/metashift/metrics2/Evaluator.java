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

import com.lge.plugins.metashift.models.Collectable;

/**
 * Evaluator class.
 *
 * @author Sung Gon Kim
 */
public abstract class Evaluator<T> extends Counter {

  /**
   * Represents the threshold.
   */
  private final double threshold;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public Evaluator(final double threshold) {
    this.threshold = threshold;
  }

  /**
   * Returns the threshold.
   *
   * @return threshold value
   */
  public final double getThreshold() {
    return threshold;
  }

  /**
   * Returns whether the metric is available.
   *
   * @return true if the metric is available, false otherwise
   */
  public final boolean isAvailable() {
    return getDenominator() > 0;
  }

  /**
   * Returns whether the evaluation of the metric meets the criteria.
   *
   * @return true if the metric meets the criteria, false otherwise
   */
  public boolean isQualified() {
    return isAvailable() && (double) getNumerator() / (double) getDenominator() >= getThreshold();
  }

  /**
   * Parse the given object to create metric data.
   *
   * @param object to parse
   * @return self
   */
  @SuppressWarnings("unchecked")
  public final T parse(final Collectable object) {
    setDenominator(0);
    setNumerator(0);
    parseImpl(object);
    return (T) this;
  }

  /**
   * Evaluate the given object.
   *
   * @param c object to evaluate
   */
  protected abstract void parseImpl(final Collectable c);
}
