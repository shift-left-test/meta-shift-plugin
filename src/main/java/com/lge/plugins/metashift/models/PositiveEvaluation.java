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

/**
 * PositiveEvaluation class.
 *
 * @author Sung Gon Kim
 */
public class PositiveEvaluation extends Evaluation {

  private static final long serialVersionUID = 4615228681404727028L;

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   * @param tolerance   value
   */
  public PositiveEvaluation(boolean available, long denominator, long numerator, double threshold,
      long tolerance) {
    super(available, denominator, numerator, threshold, tolerance, Type.POSITIVE);
  }

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   */
  public PositiveEvaluation(boolean available, long denominator, long numerator, double threshold) {
    this(available, denominator, numerator, threshold, 0);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public PositiveEvaluation(Evaluation other) {
    super(other);
  }

  @Override
  public boolean isQualified() {
    if (!isAvailable()) {
      return false;
    }
    return getRatio() >= getThreshold();
  }
}
