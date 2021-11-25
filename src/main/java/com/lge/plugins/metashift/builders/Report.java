/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.models.Aggregate;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Report class.
 *
 * @param <T> return type
 * @author Sung Gon Kim
 */
public abstract class Report<T> implements Aggregate<T>, Serializable {

  private static final long serialVersionUID = -8117343237092632763L;

  private final Map<Metric, T> groups;

  /**
   * Default constructor.
   */
  public Report() {
    this.groups = new EnumMap<>(Metric.class);
  }

  protected void put(Metric metric, T object) {
    groups.put(metric, object);
  }

  @Override
  public T getPremirrorCache() {
    return groups.get(Metric.PREMIRROR_CACHE);
  }

  @Override
  public T getSharedStateCache() {
    return groups.get(Metric.SHARED_STATE_CACHE);
  }

  @Override
  public T getRecipeViolations() {
    return groups.get(Metric.RECIPE_VIOLATIONS);
  }

  @Override
  public T getComments() {
    return groups.get(Metric.COMMENTS);
  }

  @Override
  public T getCodeViolations() {
    return groups.get(Metric.CODE_VIOLATIONS);
  }

  @Override
  public T getComplexity() {
    return groups.get(Metric.COMPLEXITY);
  }

  @Override
  public T getDuplications() {
    return groups.get(Metric.DUPLICATIONS);
  }

  @Override
  public T getUnitTests() {
    return groups.get(Metric.UNIT_TESTS);
  }

  @Override
  public T getStatementCoverage() {
    return groups.get(Metric.STATEMENT_COVERAGE);
  }

  @Override
  public T getBranchCoverage() {
    return groups.get(Metric.BRANCH_COVERAGE);
  }

  @Override
  public T getMutationTests() {
    return groups.get(Metric.MUTATION_TESTS);
  }
}
