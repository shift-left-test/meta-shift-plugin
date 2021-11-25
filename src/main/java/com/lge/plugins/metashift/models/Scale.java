/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.io.Serializable;

/**
 * Scale class.
 *
 * @author Sung Gon Kim
 */
public class Scale implements Serializable {

  private static final long serialVersionUID = 6152247773057621263L;

  private final long denominator;
  private final long numerator;

  /**
   * Default constructor.
   *
   * @param denominator value
   * @param numerator   value
   */
  public Scale(long denominator, long numerator) {
    this.denominator = denominator;
    this.numerator = numerator;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Scale(Scale other) {
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
