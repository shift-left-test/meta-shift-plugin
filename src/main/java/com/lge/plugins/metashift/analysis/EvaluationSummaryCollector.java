/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
