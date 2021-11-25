/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * NegativeEvaluation class.
 *
 * @author Sung Gon Kim
 */
public class NegativeEvaluation extends Evaluation {

  private static final long serialVersionUID = -7656665738808949194L;

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   * @param tolerance   value
   */
  public NegativeEvaluation(boolean available, long denominator, long numerator, double threshold,
      long tolerance) {
    super(available, denominator, numerator, threshold, tolerance, Type.NEGATIVE);
  }

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   */
  public NegativeEvaluation(boolean available, long denominator, long numerator, double threshold) {
    this(available, denominator, numerator, threshold, 0);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public NegativeEvaluation(Evaluation other) {
    super(other);
  }

  @Override
  public boolean isQualified() {
    if (!isAvailable()) {
      return false;
    }
    if (getDenominator() == 0) {
      return true;
    }
    return getRatio() <= getThreshold();
  }
}
