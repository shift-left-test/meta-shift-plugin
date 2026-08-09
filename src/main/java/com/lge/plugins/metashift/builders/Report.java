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

  /**
   * Returns the group of the given metric, or null when this report did not fully deserialize.
   *
   * <p>Jenkins' {@code RobustReflectionConverter} keeps a build record loadable when one of its
   * fields fails to convert, but it populates the object by reflection rather than through a
   * constructor: a report that hit such a failure is handed back with {@code groups} — and every
   * field written after it — left null, despite being declared final. Callers therefore have to
   * treat a missing group as "no data" instead of assuming the map exists.
   *
   * @param metric to look up
   * @return group, or null when unavailable
   */
  private T get(Metric metric) {
    return groups == null ? null : groups.get(metric);
  }

  @Override
  public T getUnitTests() {
    return get(Metric.UNIT_TESTS);
  }

  @Override
  public T getStatementCoverage() {
    return get(Metric.STATEMENT_COVERAGE);
  }

  @Override
  public T getBranchCoverage() {
    return get(Metric.BRANCH_COVERAGE);
  }

  @Override
  public T getMutationTests() {
    return get(Metric.MUTATION_TESTS);
  }
}
