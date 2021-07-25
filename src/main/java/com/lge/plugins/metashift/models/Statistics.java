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

/**
 * Statistics class.
 *
 * @author Sung Gon Kim
 */
public class Statistics implements Serializable {

  private static final long serialVersionUID = -2466325184329532321L;

  private final double min;
  private final double average;
  private final double max;
  private final double threshold;
  private final double value;

  /**
   * Default constructor.
   *
   * @param min       value
   * @param average   value
   * @param max       value
   * @param threshold value
   * @param value     of the data
   */
  public Statistics(double min, double average, double max, double threshold, double value) {
    this.min = min;
    this.average = average;
    this.max = max;
    this.threshold = threshold;
    this.value = value;
  }

  /**
   * Default constructor.
   *
   * @param source    object
   * @param threshold value
   * @param value     of the data
   */
  public Statistics(DoubleSummaryStatistics source, double threshold, double value) {
    this.min = source.getMin() == Double.POSITIVE_INFINITY ? 0.0 : source.getMin();
    this.average = source.getAverage();
    this.max = source.getMax() == Double.NEGATIVE_INFINITY ? 0.0 : source.getMax();
    this.threshold = threshold;
    this.value = value;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Statistics(Statistics other) {
    this.min = other.min;
    this.average = other.average;
    this.max = other.max;
    this.threshold = other.threshold;
    this.value = other.value;
  }

  /**
   * Returns the minimum value.
   *
   * @return minimum value
   */
  public double getMin() {
    return min;
  }

  /**
   * Returns the average value.
   *
   * @return average value
   */
  public double getAverage() {
    return average;
  }

  /**
   * Returns the maximum value.
   *
   * @return maximum value
   */
  public double getMax() {
    return max;
  }

  /**
   * Returns the threshold value.
   *
   * @return threshold value
   */
  public double getThreshold() {
    return threshold;
  }

  /**
   * Returns the value of the data.
   *
   * @return data value
   */
  public double getValue() {
    return value;
  }
}
