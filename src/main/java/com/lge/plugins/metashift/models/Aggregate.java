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
    UNIT_TESTS,
    STATEMENT_COVERAGE,
    BRANCH_COVERAGE,
    MUTATION_TESTS
  }

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
