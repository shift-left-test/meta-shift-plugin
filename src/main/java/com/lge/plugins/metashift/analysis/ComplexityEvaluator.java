/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * ComplexityEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public ComplexityEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(CodeSizeData.class) && s.contains(ComplexityData.class);
    long denominator = s.objects(ComplexityData.class).count();
    long tolerance = configuration.getComplexityTolerance();
    long numerator = s.objects(ComplexityData.class).filter(o -> o.getValue() >= tolerance).count();
    double threshold = (double) configuration.getComplexityThreshold() / 100.0;
    return new NegativeEvaluation(available, denominator, numerator, threshold, tolerance);
  }
}
