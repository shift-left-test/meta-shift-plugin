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

package com.lge.plugins.metashift.models;

import hudson.model.Result;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * BuildStatusSummary class.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusSummary implements Serializable {

  private static final long serialVersionUID = -5343542394256059024L;

  /**
   * Represents the metric type.
   */
  private enum Type {
    PREMIRROR_CACHE,
    SHARED_STATE_CACHE,
    RECIPE_VIOLATIONS,
    COMMENTS,
    CODE_VIOLATIONS,
    COMPLEXITY,
    DUPLICATIONS,
    UNIT_TESTS,
    STATEMENT_COVERAGE,
    BRANCH_COVERAGE,
    MUTATION_TESTS
  }

  private final Map<Type, Result> status;

  /**
   * Default constructor.
   *
   * @param premirrorCache    build status
   * @param sharedStateCache  build status
   * @param recipeViolations  build status
   * @param comments          build status
   * @param codeViolations    build status
   * @param complexity        build status
   * @param duplications      build status
   * @param unitTests         build status
   * @param statementCoverage build status
   * @param branchCoverage    build status
   * @param mutationTests     build status
   */
  public BuildStatusSummary(Result premirrorCache, Result sharedStateCache, Result recipeViolations,
      Result comments, Result codeViolations, Result complexity, Result duplications,
      Result unitTests, Result statementCoverage, Result branchCoverage, Result mutationTests) {
    this();
    status.put(Type.PREMIRROR_CACHE, premirrorCache);
    status.put(Type.SHARED_STATE_CACHE, sharedStateCache);
    status.put(Type.RECIPE_VIOLATIONS, recipeViolations);
    status.put(Type.COMMENTS, comments);
    status.put(Type.CODE_VIOLATIONS, codeViolations);
    status.put(Type.COMPLEXITY, complexity);
    status.put(Type.DUPLICATIONS, duplications);
    status.put(Type.UNIT_TESTS, unitTests);
    status.put(Type.STATEMENT_COVERAGE, statementCoverage);
    status.put(Type.BRANCH_COVERAGE, branchCoverage);
    status.put(Type.MUTATION_TESTS, mutationTests);
  }

  /**
   * Default constructor.
   */
  public BuildStatusSummary() {
    status = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> status.put(type, Result.SUCCESS));
  }

  /**
   * Returns the desired build status by the premirror cache.
   *
   * @return build status
   */
  public Result getPremirrorCache() {
    return status.get(Type.PREMIRROR_CACHE);
  }

  /**
   * Returns the desired build status by the shared state cache.
   *
   * @return build status
   */
  public Result getSharedStateCache() {
    return status.get(Type.SHARED_STATE_CACHE);
  }

  /**
   * Returns the desired build status by the recipe violations.
   *
   * @return build status
   */
  public Result getRecipeViolations() {
    return status.get(Type.RECIPE_VIOLATIONS);
  }

  /**
   * Returns the desired build status by the comments.
   *
   * @return build status
   */
  public Result getComments() {
    return status.get(Type.COMMENTS);
  }

  /**
   * Returns the desired build status by the code violations.
   *
   * @return build status
   */
  public Result getCodeViolations() {
    return status.get(Type.CODE_VIOLATIONS);
  }

  /**
   * Returns the desired build status by the complexity.
   *
   * @return build status
   */
  public Result getComplexity() {
    return status.get(Type.COMPLEXITY);
  }

  /**
   * Returns the desired build status by the duplications.
   *
   * @return build status
   */
  public Result getDuplications() {
    return status.get(Type.DUPLICATIONS);
  }

  /**
   * Returns the desired build status by the unit tests.
   *
   * @return build status
   */
  public Result getUnitTests() {
    return status.get(Type.UNIT_TESTS);
  }

  /**
   * Returns the desired build status by the statement coverage.
   *
   * @return build status
   */
  public Result getStatementCoverage() {
    return status.get(Type.STATEMENT_COVERAGE);
  }

  /**
   * Returns the desired build status by the branch coverage.
   *
   * @return build status
   */
  public Result getBranchCoverage() {
    return status.get(Type.BRANCH_COVERAGE);
  }

  /**
   * Returns the desired build status by the mutation tests.
   *
   * @return build status
   */
  public Result getMutationTests() {
    return status.get(Type.MUTATION_TESTS);
  }

  /**
   * Returns the combined build status of the metrics.
   *
   * @return build status
   */
  public Result getCombined() {
    return status.values().stream().reduce(Result.SUCCESS, (a, b) -> a.combine(b));
  }
}
