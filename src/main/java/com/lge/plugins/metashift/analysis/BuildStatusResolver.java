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

import com.lge.plugins.metashift.common.Aggregate;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Recipes;
import hudson.model.Result;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class BuildStatusResolver implements Aggregate<Result>, Collector<Recipes, Void> {

  private final Configuration configuration;
  private final Map<Type, Result> status;

  /**
   * Default constructor.
   *
   * @param configuration for build status
   */
  public BuildStatusResolver(Configuration configuration) {
    this.configuration = configuration;
    status = new EnumMap<>(Aggregate.Type.class);
    Stream.of(Type.values()).forEach(o -> status.put(o, Result.SUCCESS));
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
    return !unstable || !available || qualified ? Result.SUCCESS : Result.UNSTABLE;
  }

  @Override
  public Result getPremirrorCache() {
    return status.get(Type.PREMIRROR_CACHE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setPremirrorCache(Recipes recipes) {
    status.put(Type.PREMIRROR_CACHE,
        toResult(configuration.isPremirrorCacheAsUnstable(),
            new PremirrorCacheEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getSharedStateCache() {
    return status.get(Type.SHARED_STATE_CACHE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setSharedStateCache(Recipes recipes) {
    status.put(Type.SHARED_STATE_CACHE,
        toResult(configuration.isSharedStateCacheAsUnstable(),
            new SharedStateCacheEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getRecipeViolations() {
    return status.get(Type.RECIPE_VIOLATIONS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setRecipeViolations(Recipes recipes) {
    status.put(Type.RECIPE_VIOLATIONS,
        toResult(configuration.isRecipeViolationsAsUnstable(),
            new RecipeViolationEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getComments() {
    return status.get(Type.COMMENTS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setComments(Recipes recipes) {
    status.put(Type.COMMENTS,
        toResult(configuration.isCommentsAsUnstable(),
            new CommentEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getCodeViolations() {
    return status.get(Type.CODE_VIOLATIONS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setCodeViolations(Recipes recipes) {
    status.put(Type.CODE_VIOLATIONS,
        toResult(configuration.isCodeViolationsAsUnstable(),
            new CodeViolationEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getComplexity() {
    return status.get(Type.COMPLEXITY);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setComplexity(Recipes recipes) {
    status.put(Type.COMPLEXITY,
        toResult(configuration.isComplexityAsUnstable(),
            new ComplexityEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getDuplications() {
    return status.get(Type.DUPLICATIONS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setDuplications(Recipes recipes) {
    status.put(Type.DUPLICATIONS,
        toResult(configuration.isDuplicationsAsUnstable(),
            new DuplicationEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getUnitTests() {
    return status.get(Type.UNIT_TESTS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setUnitTests(Recipes recipes) {
    status.put(Type.UNIT_TESTS,
        toResult(configuration.isTestAsUnstable(),
            new UnitTestEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getStatementCoverage() {
    return status.get(Type.STATEMENT_COVERAGE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setStatementCoverage(Recipes recipes) {
    status.put(Type.STATEMENT_COVERAGE,
        toResult(configuration.isStatementCoverageAsUnstable(),
            new StatementCoverageEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getBranchCoverage() {
    return status.get(Type.BRANCH_COVERAGE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setBranchCoverage(Recipes recipes) {
    status.put(Type.BRANCH_COVERAGE,
        toResult(configuration.isBranchCoverageAsUnstable(),
            new BranchCoverageEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getMutationTests() {
    return status.get(Type.MUTATION_TESTS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setMutationTests(Recipes recipes) {
    status.put(Type.MUTATION_TESTS,
        toResult(configuration.isMutationTestAsUnstable(),
            new MutationTestEvaluator(configuration).parse(recipes)));
  }

  /**
   * Returns the combined build status.
   *
   * @return build status
   */
  public Result getCombined() {
    return status.values().stream().reduce(Result.SUCCESS, (a, b) -> a.combine(b));
  }

  @Override
  public Void parse(Recipes recipes) {
    setPremirrorCache(recipes);
    setSharedStateCache(recipes);
    setRecipeViolations(recipes);
    setComments(recipes);
    setCodeViolations(recipes);
    setComplexity(recipes);
    setDuplications(recipes);
    setUnitTests(recipes);
    setStatementCoverage(recipes);
    setBranchCoverage(recipes);
    setMutationTests(recipes);
    return null;
  }
}
