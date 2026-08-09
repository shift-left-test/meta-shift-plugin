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
   *
   * <p>These constants are the keys of {@code Report.groups}, an {@code EnumMap} persisted into
   * every build record, and the value of the persisted {@code Group.metric} field. XStream
   * resolves both by name through {@code Enum.valueOf}, and a single unknown name makes
   * {@code RobustReflectionConverter} abandon the enclosing report: the object is still created
   * but {@code groups} and every field serialized after it stay null, so the report page silently
   * renders without any metrics. Constants that older releases wrote therefore have to stay here
   * even though nothing builds them any more.
   */
  enum Metric {
    NONE,
    // Retained for backward compatibility only; removed as built metrics in 5dd0ef0.
    PREMIRROR_CACHE,
    SHARED_STATE_CACHE,
    RECIPE_VIOLATIONS,
    COMMENTS,
    CODE_VIOLATIONS,
    COMPLEXITY,
    DUPLICATIONS,
    // Retained for backward compatibility only; removed as a built metric in 62c3312.
    TESTED_RECIPES,
    UNIT_TESTS,
    STATEMENT_COVERAGE,
    BRANCH_COVERAGE,
    MUTATION_TESTS;

    /**
     * Returns the metrics a report is built for, i.e. everything except the constants kept only
     * so that build records published by earlier releases still deserialize. Reports iterate this
     * rather than {@link #values()} so a compatibility constant never reaches a new build record.
     *
     * @return metrics currently built
     */
    static Metric[] active() {
      return new Metric[]{
          NONE, UNIT_TESTS, STATEMENT_COVERAGE, BRANCH_COVERAGE, MUTATION_TESTS,
      };
    }
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
    /**
     * The failed and errored unit tests of the whole project, capped, for the build page's Tests
     * tab. Precomputed while publishing so that rendering the page does not have to read back and
     * filter every recipe's {@link #SUMMARIES}.
     */
    FAILED_OBJECTS,
    /**
     * The survived mutations of the whole project, capped, for the build page's Mutations tab.
     * The counterpart of {@link #FAILED_OBJECTS}.
     */
    SURVIVED_OBJECTS,
    /**
     * Untruncated totals behind {@link #FAILED_OBJECTS} and {@link #SURVIVED_OBJECTS}, so the
     * page can show the real counts and say when a list was capped.
     */
    COUNTS,
  }
}
