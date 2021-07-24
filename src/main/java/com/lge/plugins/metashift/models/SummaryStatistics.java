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
import java.util.DoubleSummaryStatistics;
import net.sf.json.JSONObject;

/**
 * The SummaryStatistics class.
 *
 * @author Sung Gon Kim
 */
public class SummaryStatistics implements Serializable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 7310657096997182332L;

  /**
   * Represents the count of data.
   */
  private final long count;

  /**
   * Represents the sum of data.
   */
  private final double sum;

  /**
   * Represents the minimum value of data.
   */
  private final double min;

  /**
   * Represents the maximum value of data.
   */
  private final double max;

  /**
   * Represents the average of data.
   */
  private final double average;

  /**
   * Default constructor.
   */
  public SummaryStatistics() {
    count = 0;
    sum = 0.0;
    min = 0.0;
    max = 0.0;
    average = 0.0;
  }

  /**
   * Default constructor.
   *
   * @param source data
   */
  public SummaryStatistics(final DoubleSummaryStatistics source) {
    count = source.getCount();
    sum = source.getSum();
    min = source.getMin() == Double.POSITIVE_INFINITY ? 0.0 : source.getMin();
    max = source.getMax() == Double.NEGATIVE_INFINITY ? 0.0 : source.getMax();
    average = source.getAverage();
  }

  /**
   * Returns the count of data.
   *
   * @return count value
   */
  public long getCount() {
    return count;
  }

  /**
   * Returns the sum of data.
   *
   * @return sum value
   */
  public double getSum() {
    return sum;
  }

  /**
   * Returns the minimum of data.
   *
   * @return minimum value
   */
  public double getMin() {
    return min;
  }

  /**
   * Returns the maximum of data.
   *
   * @return maximum value
   */
  public double getMax() {
    return max;
  }

  /**
   * Returns the average of data.
   *
   * @return average value
   */
  public double getAverage() {
    return average;
  }

  /**
   * Returns the object as a JSONObject instance.
   *
   * @return a JSONObject instance
   */
  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}
