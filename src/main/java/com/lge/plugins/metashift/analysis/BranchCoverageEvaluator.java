/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.TestData;

/**
 * BranchCoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class BranchCoverageEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public BranchCoverageEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(TestData.class) && s.contains(BranchCoverageData.class);
    long denominator = s.objects(BranchCoverageData.class).count();
    long numerator = s.objects(BranchCoverageData.class).filter(CoverageData::isCovered).count();
    double threshold = (double) configuration.getBranchCoverageThreshold() / 100.0;
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }
}
