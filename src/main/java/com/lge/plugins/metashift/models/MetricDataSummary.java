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
 * MetricDataSummary class.
 *
 * @author Sung Gon Kim
 */
public class MetricDataSummary implements Serializable {

  private static final long serialVersionUID = -5243933450178054221L;

  private final Evaluation evaluation;
  private final Statistics statistics;
  private final Distribution distribution;

  /**
   * Default constructor.
   *
   * @param evaluation   object
   * @param statistics   object
   * @param distribution object
   */
  public MetricDataSummary(Evaluation evaluation, Statistics statistics,
      Distribution distribution) {
    this.evaluation = evaluation;
    this.statistics = statistics;
    this.distribution = distribution;
  }

  /**
   * Default constructor.
   */
  public MetricDataSummary() {
    this(
        new PositiveEvaluation(false, 0, 0, 0),
        new Statistics(new DoubleSummaryStatistics(), 0.0),
        new Distribution(0, 0)
    );
  }

  /**
   * Returns the evaluation object.
   *
   * @return evaluation object
   */
  public Evaluation getEvaluation() {
    return evaluation;
  }

  /**
   * Returns the statistics object.
   *
   * @return statistics object
   */
  public Statistics getStatistics() {
    return statistics;
  }

  /**
   * Returns the distribution object.
   *
   * @return distribution object
   */
  public Distribution getDistribution() {
    return distribution;
  }
}
