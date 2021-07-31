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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DataSummary class.
 *
 * @author Sung Gon Kim
 */
public class DataSummary extends Data {

  private static final long serialVersionUID = 6768623147726489246L;

  private final long linesOfCode;
  private final long total;
  private final long first;
  private final long second;
  private final long third;
  private final long fourth;
  private final double ratio;
  private final boolean qualified;

  /**
   * Default constructor.
   *
   * @param name of the data
   */
  public DataSummary(String name, LinesOfCode linesOfCode, Distribution distribution,
      Evaluation evaluation) {
    super(name);
    this.linesOfCode = linesOfCode.getLines();
    this.total = distribution.getTotal();
    this.first = distribution.getFirst().getCount();
    this.second = distribution.getSecond().getCount();
    this.third = distribution.getThird().getCount();
    this.fourth = distribution.getFourth().getCount();
    this.ratio = evaluation.getRatio();
    this.qualified = evaluation.isQualified();
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public long getLinesOfCode() {
    return linesOfCode;
  }

  /**
   * Returns the total number of data.
   *
   * @return total number of data
   */
  public long getTotal() {
    return total;
  }

  /**
   * Returns the number of first element.
   *
   * @return the number of first element
   */
  public long getFirst() {
    return first;
  }

  /**
   * Returns the number of second element.
   *
   * @return the number of second element
   */
  public long getSecond() {
    return second;
  }

  /**
   * Returns the number of third element.
   *
   * @return the number of third element
   */
  public long getThird() {
    return third;
  }

  /**
   * Returns the number of fourth element.
   *
   * @return the number of fourth element
   */
  public long getFourth() {
    return fourth;
  }

  /**
   * Returns the ratio value.
   *
   * @return ratio
   */
  public double getRatio() {
    return ratio;
  }

  /**
   * Returns the evaluation status.
   *
   * @return evaluation status
   */
  public boolean isQualified() {
    return qualified;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    DataSummary other = (DataSummary) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .toHashCode();
  }
}
