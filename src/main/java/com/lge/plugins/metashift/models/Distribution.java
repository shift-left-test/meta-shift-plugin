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
 * Distribution class.
 *
 * @author Sung Gon Kim
 */
public class Distribution implements Serializable {

  private static final long serialVersionUID = -5877880387901811750L;

  private final Scale first;
  private final Scale second;
  private final Scale third;
  private final Scale fourth;
  private final long total;

  /**
   * Default constructor.
   *
   * @param firstNumerator  value
   * @param secondNumerator value
   * @param thirdNumerator  value
   * @param fourthNumerator value
   */
  public Distribution(long firstNumerator, long secondNumerator, long thirdNumerator,
      long fourthNumerator) {
    total = firstNumerator + secondNumerator + thirdNumerator + fourthNumerator;
    first = new Scale(total, firstNumerator);
    second = new Scale(total, secondNumerator);
    third = new Scale(total, thirdNumerator);
    fourth = new Scale(total, fourthNumerator);
  }

  /**
   * Default constructor.
   *
   * @param firstNumerator  value
   * @param secondNumerator value
   * @param thirdNumerator  value
   */
  public Distribution(long firstNumerator, long secondNumerator, long thirdNumerator) {
    this(firstNumerator, secondNumerator, thirdNumerator, 0);
  }

  /**
   * Default constructor.
   *
   * @param firstNumerator  value
   * @param secondNumerator value
   */
  public Distribution(long firstNumerator, long secondNumerator) {
    this(firstNumerator, secondNumerator, 0);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Distribution(Distribution other) {
    this.first = new Scale(other.first);
    this.second = new Scale(other.second);
    this.third = new Scale(other.third);
    this.fourth = new Scale(other.fourth);
    this.total = other.total;
  }

  /**
   * Returns the first counter.
   *
   * @return counter object
   */
  public Scale getFirst() {
    return first;
  }

  /**
   * Returns the second counter.
   *
   * @return counter object
   */
  public Scale getSecond() {
    return second;
  }

  /**
   * Returns the third counter.
   *
   * @return counter object
   */
  public Scale getThird() {
    return third;
  }

  /**
   * Returns the fourth counter.
   *
   * @return counter object
   */
  public Scale getFourth() {
    return fourth;
  }

  /**
   * Returns the number in total.
   *
   * @return total number
   */
  public long getTotal() {
    return total;
  }
}
