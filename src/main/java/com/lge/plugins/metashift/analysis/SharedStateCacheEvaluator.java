/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * SharedStateCacheEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public SharedStateCacheEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(SharedStateCacheData.class);
    long denominator = s.objects(SharedStateCacheData.class).distinct().count();
    long numerator = s.objects(SharedStateCacheData.class).distinct()
        .filter(SharedStateCacheData::isAvailable).count();
    double threshold = (double) configuration.getSharedStateCacheThreshold() / 100.0;
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }
}
