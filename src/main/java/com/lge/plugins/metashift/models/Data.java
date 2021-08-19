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
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Represents a data object.
 *
 * @author Sung Gon Kim
 */
public abstract class Data implements Serializable, Comparable<Data> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7672797936775946949L;

  /**
   * Represents the name of the data.
   */
  private final String name;

  /**
   * Default constructor.
   *
   * @param name of the data
   */
  public Data(final String name) {
    this.name = name;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return true if this is the same as the other, false otherwise
   */
  @Override
  public abstract boolean equals(Object object);

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public abstract int hashCode();

  @Override
  public int compareTo(Data other) {
    return new CompareToBuilder()
        .append(getName(), other.getName())
        .toComparison();
  }

  /**
   * Returns the name of the data.
   *
   * @return the name of the data
   */
  public String getName() {
    return name;
  }
}
