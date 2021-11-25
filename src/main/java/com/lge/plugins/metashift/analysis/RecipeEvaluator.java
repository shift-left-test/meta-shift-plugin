/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Streamable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RecipeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluator implements Evaluator {

  private final List<Evaluator> evaluators;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public RecipeEvaluator(Configuration configuration) {
    this.evaluators = Arrays.asList(
        new PremirrorCacheEvaluator(configuration),
        new SharedStateCacheEvaluator(configuration),
        new RecipeViolationEvaluator(configuration),
        new CommentEvaluator(configuration),
        new CodeViolationEvaluator(configuration),
        new ComplexityEvaluator(configuration),
        new DuplicationEvaluator(configuration),
        new UnitTestEvaluator(configuration),
        new StatementCoverageEvaluator(configuration),
        new BranchCoverageEvaluator(configuration),
        new MutationTestEvaluator(configuration)
    );
  }

  @Override
  public Evaluation parse(Streamable s) {
    List<Evaluation> evaluations = evaluators.stream()
        .map(o -> o.parse(s)).collect(Collectors.toList());
    long denominator = evaluations.stream().filter(Evaluation::isAvailable).count();
    long numerator = evaluations.stream().filter(Evaluation::isQualified).count();
    return new PositiveEvaluation(denominator > 0, denominator, numerator, 1.0);
  }
}
