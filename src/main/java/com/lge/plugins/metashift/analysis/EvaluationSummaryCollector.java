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
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Streamable;

/**
 * EvaluationSummaryCollector class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummaryCollector implements Collector<Recipe, EvaluationSummary> {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public EvaluationSummaryCollector(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Parses the list of recipes to create the evaluation summary.
   *
   * @param recipes to parse
   * @return evaluation summary
   */
  public EvaluationSummary parse(Recipes recipes) {
    return createEvaluationSummary("", recipes);
  }

  @Override
  public EvaluationSummary parse(Recipe recipe) {
    return createEvaluationSummary(recipe.getName(), recipe);
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private EvaluationSummary createEvaluationSummary(String name, Streamable o) {
    return new EvaluationSummary(
        name,
        new LinesOfCodeCollector().parse(o),
        new PremirrorCacheEvaluator(configuration).parse(o),
        new SharedStateCacheEvaluator(configuration).parse(o),
        new RecipeViolationEvaluator(configuration).parse(o),
        new CommentEvaluator(configuration).parse(o),
        new CodeViolationEvaluator(configuration).parse(o),
        new ComplexityEvaluator(configuration).parse(o),
        new DuplicationEvaluator(configuration).parse(o),
        new UnitTestEvaluator(configuration).parse(o),
        new StatementCoverageEvaluator(configuration).parse(o),
        new BranchCoverageEvaluator(configuration).parse(o),
        new MutationTestEvaluator(configuration).parse(o)
    );
  }
}
