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
 * Counter class.
 *
 * @author Sung Gon Kim
 */
public class Counter implements Serializable {

  private static final long serialVersionUID = 6152247773057621263L;

  private final long denominator;
  private final long numerator;

  /**
   * Default constructor.
   *
   * @param denominator value
   * @param numerator   value
   */
  public Counter(long denominator, long numerator) {
    this.denominator = denominator;
    this.numerator = numerator;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Counter(Counter other) {
    this.denominator = other.denominator;
    this.numerator = other.numerator;
  }

  /**
   * Returns the numerator value.
   *
   * @return numerator
   */
  public long getCount() {
    return this.numerator;
  }

  /**
   * Returns the ratio of the value.
   *
   * @return ratio
   */
  public double getRatio() {
    return denominator > 0 ? (double) numerator / denominator : 0.0;
  }
}
