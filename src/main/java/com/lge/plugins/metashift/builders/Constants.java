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
    UNIT_TESTS,
    STATEMENT_COVERAGE,
    BRANCH_COVERAGE,
    MUTATION_TESTS,
  }

  /**
   * Represents data types.
   */
  enum Data {
    EVALUATION,
    DISTRIBUTION,
    SUMMARIES,
    OBJECTS,
    FILE,
    /**
     * Every object of a recipe's metric, flattened across files (unlike {@link #OBJECTS}, which
     * is keyed per file). Used to list a recipe's mutations on a single page.
     */
    FLAT_OBJECTS,
  }
}
