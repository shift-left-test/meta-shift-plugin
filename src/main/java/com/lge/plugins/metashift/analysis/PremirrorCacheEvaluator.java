/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * PremirrorCacheEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public PremirrorCacheEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(PremirrorCacheData.class);
    long denominator = s.objects(PremirrorCacheData.class).distinct().count();
    long numerator = s.objects(PremirrorCacheData.class).distinct()
        .filter(PremirrorCacheData::isAvailable).count();
    double threshold = (double) configuration.getPremirrorCacheThreshold() / 100.0;
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }
}
