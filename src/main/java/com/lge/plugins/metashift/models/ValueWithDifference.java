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
 * A class which holds the value and the difference.
 *
 * @author Sung Gon Kim
 */
public class ValueWithDifference<T extends Number> implements Serializable {

  private static final long serialVersionUID = 5696129323489009062L;

  private final T value;
  private final T difference;

  /**
   * Default constructor.
   *
   * @param value      data
   * @param difference data
   */
  public ValueWithDifference(T value, T difference) {
    this.value = value;
    this.difference = difference;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public ValueWithDifference(ValueWithDifference<T> other) {
    this.value = other.value;
    this.difference = other.difference;
  }

  /**
   * Returns the value data.
   *
   * @return value data
   */
  public T getValue() {
    return value;
  }

  /**
   * Returns the difference data.
   *
   * @return difference data
   */
  public T getDifference() {
    return difference;
  }
}
