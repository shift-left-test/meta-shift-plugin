/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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

  /**
   * Default constructor.
   *
   * @param source object
   */
  public Statistics(DoubleSummaryStatistics source) {
    this.min = source.getMin() == Double.POSITIVE_INFINITY ? 0.0 : source.getMin();
    this.average = source.getAverage();
    this.max = source.getMax() == Double.NEGATIVE_INFINITY ? 0.0 : source.getMax();
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
}
