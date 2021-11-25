/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public RecipeViolationEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available =
        s.contains(RecipeSizeData.class) && s.contains(RecipeViolationData.class);
    long denominator = s.objects(RecipeSizeData.class).mapToLong(RecipeSizeData::getLines).sum();
    long numerator = s.objects(RecipeViolationData.class).count();
    double threshold = configuration.getRecipeViolationThreshold();
    return new NegativeEvaluation(available, denominator, numerator, threshold);
  }
}
