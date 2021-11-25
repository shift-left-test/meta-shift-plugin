/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
