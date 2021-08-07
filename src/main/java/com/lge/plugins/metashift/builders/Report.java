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
