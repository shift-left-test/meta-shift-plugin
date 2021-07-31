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

/**
 * Aggregate interface.
 *
 * @param <T> return type
 * @author Sung Gon Kim
 */
public interface Aggregate<T> {

  /**
   * Represents the aggregate type.
   */
  enum Type {
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

  /**
   * Returns the premirror cache data.
   *
   * @return data
   */
  T getPremirrorCache();

  /**
   * Returns the shared state cache data.
   *
   * @return data
   */
  T getSharedStateCache();

  /**
   * Returns the recipe violation data.
   *
   * @return data
   */
  T getRecipeViolations();

  /**
   * Returns the comment data.
   *
   * @return data
   */
  T getComments();

  /**
   * Returns the code violation data.
   *
   * @return data
   */
  T getCodeViolations();

  /**
   * Returns the complexity data.
   *
   * @return data
   */
  T getComplexity();

  /**
   * Returns the duplication data.
   *
   * @return data
   */
  T getDuplications();

  /**
   * Returns the unit test data.
   *
   * @return data
   */
  T getUnitTests();

  /**
   * Returns the statement coverage data.
   *
   * @return data
   */
  T getStatementCoverage();

  /**
   * Returns the branch coverage data.
   *
   * @return data
   */
  T getBranchCoverage();

  /**
   * Returns the mutation test data.
   *
   * @return data
   */
  T getMutationTests();
}
