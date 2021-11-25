/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CodeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public CodeViolationEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(CodeSizeData.class) && s.contains(CodeViolationData.class);
    long denominator = s.objects(CodeSizeData.class).mapToLong(CodeSizeData::getLines).sum();
    long numerator = s.objects(CodeViolationData.class).count();
    double threshold = configuration.getCodeViolationThreshold();
    return new NegativeEvaluation(available, denominator, numerator, threshold);
  }
}
