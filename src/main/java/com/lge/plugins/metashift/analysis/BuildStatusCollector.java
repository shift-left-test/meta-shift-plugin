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

import com.lge.plugins.metashift.models.BuildStatusSummary;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Recipes;
import hudson.model.Result;

/**
 * BuildStatusCollector class.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusCollector implements Collector<Recipes, BuildStatusSummary> {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public BuildStatusCollector(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Returns the result object based on the inputs.
   *
   * @param unstable   status
   * @param evaluation object
   * @return result object
   */
  private Result toResult(boolean unstable, Evaluation evaluation) {
    boolean available = evaluation.isAvailable();
    boolean qualified = evaluation.isQualified();
    return (!unstable || !available || qualified) ? Result.SUCCESS : Result.UNSTABLE;
  }

  /**
   * Returns the desired build status of the premirror cache.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getPremirrorCache(Recipes recipes) {
    return toResult(configuration.isPremirrorCacheAsUnstable(),
        new PremirrorCacheEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the shared state cache.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getSharedStateCache(Recipes recipes) {
    return toResult(configuration.isSharedStateCacheAsUnstable(),
        new SharedStateCacheEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the recipe violations.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getRecipeViolations(Recipes recipes) {
    return toResult(configuration.isRecipeViolationsAsUnstable(),
        new RecipeViolationEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the comments.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getComments(Recipes recipes) {
    return toResult(configuration.isCommentsAsUnstable(),
        new CommentEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the code violations.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getCodeViolations(Recipes recipes) {
    return toResult(configuration.isCodeViolationsAsUnstable(),
        new CodeViolationEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the complexity.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getComplexity(Recipes recipes) {
    return toResult(configuration.isComplexityAsUnstable(),
        new ComplexityEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the duplications.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getDuplications(Recipes recipes) {
    return toResult(configuration.isDuplicationsAsUnstable(),
        new DuplicationEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the unit tests.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getUnitTests(Recipes recipes) {
    return toResult(configuration.isTestAsUnstable(),
        new TestEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the statement coverage.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getStatementCoverage(Recipes recipes) {
    return toResult(configuration.isStatementCoverageAsUnstable(),
        new StatementCoverageEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the branch coverage.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getBranchCoverage(Recipes recipes) {
    return toResult(configuration.isBranchCoverageAsUnstable(),
        new BranchCoverageEvaluationCollector(configuration).parse(recipes));
  }

  /**
   * Returns the desired build status of the mutation tests.
   *
   * @param recipes to parse
   * @return build status
   */
  private Result getMutationTests(Recipes recipes) {
    return toResult(configuration.isMutationTestAsUnstable(),
        new MutationTestEvaluationCollector(configuration).parse(recipes));
  }

  @Override
  public BuildStatusSummary parse(Recipes s) {
    return new BuildStatusSummary(
        getPremirrorCache(s),
        getSharedStateCache(s),
        getRecipeViolations(s),
        getComments(s),
        getCodeViolations(s),
        getComplexity(s),
        getDuplications(s),
        getUnitTests(s),
        getStatementCoverage(s),
        getBranchCoverage(s),
        getMutationTests(s));
  }
}
