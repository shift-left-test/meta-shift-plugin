/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
