/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * DuplicationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public DuplicationEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    DuplicationCalculator calculator = new DuplicationCalculator(configuration).parse(s);
    boolean available = s.contains(CodeSizeData.class) && s.contains(DuplicationData.class);
    long denominator = calculator.getLines();
    long tolerance = configuration.getDuplicationTolerance();
    long numerator = calculator.getDuplicateLines();
    double threshold = (double) configuration.getDuplicationThreshold() / 100.0;
    return new NegativeEvaluation(available, denominator, numerator, threshold, tolerance);
  }
}
