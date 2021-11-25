/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CommentEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CommentEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public CommentEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(CodeSizeData.class) && s.contains(CommentData.class);
    long denominator = s.objects(CommentData.class).mapToLong(CommentData::getLines).sum();
    long numerator = s.objects(CommentData.class).mapToLong(CommentData::getCommentLines).sum();
    double threshold = (double) configuration.getCommentThreshold() / 100.0;
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }
}
