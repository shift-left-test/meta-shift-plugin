/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.io.Serializable;

/**
 * Evaluation class.
 *
 * @author Sung Gon Kim
 */
public abstract class Evaluation implements Serializable {

  /**
   * Represents the evaluation type.
   */
  protected enum Type {
    POSITIVE,
    NEGATIVE,
  }

  private static final long serialVersionUID = 6414517999564046375L;

  private final boolean available;
  private final long denominator;
  private final long numerator;
  private final double threshold;
  private final long tolerance;
  private final Type type;

  /**
   * Default constructor.
   *
   * @param available   status
   * @param denominator value
   * @param numerator   value
   * @param threshold   value
   * @param tolerance   value
   * @param type        value
   */
  public Evaluation(boolean available, long denominator, long numerator, double threshold,
      long tolerance, Type type) {
    // A report that exists but measured nothing is not a result of zero: the parsers register a
    // type marker as soon as a report file is readable, so a test report with no test case or a
    // mutations.xml with no usable mutation would otherwise score a hard 0% and be reported as a
    // failed metric. With nothing in the denominator there is no ratio to judge, so the metric is
    // unavailable — which is what the coverage metrics already did, since CoverageParser only
    // registers the parent type their evaluators do not gate on.
    this.available = available && denominator > 0;
    this.denominator = denominator;
    this.numerator = numerator;
    this.threshold = threshold;
    this.tolerance = tolerance;
    this.type = type;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public Evaluation(Evaluation other) {
    this.available = other.available;
    this.denominator = other.denominator;
    this.numerator = other.numerator;
    this.threshold = other.threshold;
    this.tolerance = other.tolerance;
    this.type = other.type;
  }

  /**
   * Returns whether the metric is available.
   *
   * @return true if the metric is available, false otherwise
   */
  public boolean isAvailable() {
    return available;
  }

  /**
   * Returns whether the evaluation of the metric meets the criteria.
   *
   * @return true if the metric meets the criteria, false otherwise
   */
  public abstract boolean isQualified();

  /**
   * Returns the denominator value.
   *
   * @return denominator
   */
  public long getDenominator() {
    return denominator;
  }

  /**
   * Returns the numerator value.
   *
   * @return numerator
   */
  public long getNumerator() {
    return numerator;
  }

  /**
   * Returns the ratio value.
   *
   * @return ratio value
   */
  public double getRatio() {
    return (double) denominator > 0.0 ? (double) numerator / (double) denominator : 0.0;
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
   * Returns the tolerance value.
   *
   * @return tolerance value
   */
  public long getTolerance() {
    return tolerance;
  }

  /**
   * Returns the evaluation type.
   *
   * @return evaluation type
   */
  public String getType() {
    return type.name();
  }
}
