/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.TestData;

/**
 * TestedRecipeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class TestedRecipeEvaluator implements Evaluator {

  @Override
  public Evaluation parse(Streamable s) {
    long denominator = s.objects(CodeSizeData.class).map(CodeSizeData::getName).distinct().count();
    long numerator = s.objects(TestData.class).map(TestData::getName).distinct().count();
    return new PositiveEvaluation(false, denominator, numerator, 0.0);
  }
}
