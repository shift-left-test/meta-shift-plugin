/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

/**
 * Constants and enumerations class.
 *
 * @author Sung Gon Kim
 */
public class Constants {

  /**
   * Represents the scope.
   */
  enum Scope {
    PROJECT,
    RECIPE,
  }

  /**
   * Represents the metric.
   */
  enum Metric {
    NONE,
    PREMIRROR_CACHE,
    SHARED_STATE_CACHE,
    RECIPE_VIOLATIONS,
    COMMENTS,
    CODE_VIOLATIONS,
    COMPLEXITY,
    DUPLICATIONS,
    TESTED_RECIPES,
    UNIT_TESTS,
    STATEMENT_COVERAGE,
    BRANCH_COVERAGE,
    MUTATION_TESTS,
  }

  /**
   * Represents data types.
   */
  enum Data {
    LINES_OF_CODE,
    EVALUATION,
    STATISTICS,
    DISTRIBUTION,
    SUMMARIES,
    TREEMAP,
    OBJECTS,
    FILE,
  }
}
