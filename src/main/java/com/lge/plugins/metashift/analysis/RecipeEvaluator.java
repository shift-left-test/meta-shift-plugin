/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
