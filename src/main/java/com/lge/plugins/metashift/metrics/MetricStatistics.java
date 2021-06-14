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

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.Recipes;
import java.util.DoubleSummaryStatistics;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the group of statistics for each metric.
 *
 * @author Sung Gon Kim
 */
public class MetricStatistics implements Queryable<DoubleSummaryStatistics> {

  /**
   * Represents the statistics types.
   */
  private enum Type {

    /**
     * Premirror cache type.
     */
    PREMIRROR_CACHE,

    /**
     * Shared state cache type.
     */
    SHARED_STATE_CACHE,

    /**
     * Code violation type.
     */
    CODE_VIOLATIONS,

    /**
     * Comment type.
     */
    COMMENTS,

    /**
     * Complexity type.
     */
    COMPLEXITY,

    /**
     * Coverage type.
     */
    COVERAGE,

    /**
     * Duplication type.
     */
    DUPLICATIONS,

    /**
     * Mutation test type.
     */
    MUTATION_TEST,

    /**
     * Recipe violation type.
     */
    RECIPE_VIOLATIONS,

    /**
     * Test type.
     */
    TEST,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<MetricStatistics.Type, DoubleSummaryStatistics> collection;

  /**
   * Represents the criteria object.
   */
  private final Criteria criteria;

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public MetricStatistics(final Criteria criteria) {
    collection = new EnumMap<>(MetricStatistics.Type.class);
    Stream.of(MetricStatistics.Type.values())
        .forEach(type -> collection.put(type, new DoubleSummaryStatistics()));
    this.criteria = criteria;
  }

  /**
   * Parses the given recipes to create metric statistics.
   *
   * @param recipes to parse
   * @return self object
   */
  public MetricStatistics parse(final Recipes recipes) {
    List<Metrics> metrics = recipes.stream()
        .map(recipe -> new Metrics(criteria).parse(recipe))
        .collect(Collectors.toList());

    collection.put(Type.PREMIRROR_CACHE,
        metrics.stream()
            .map(Metrics::getPremirrorCache)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.SHARED_STATE_CACHE,
        metrics.stream()
            .map(Metrics::getSharedStateCache)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.CODE_VIOLATIONS,
        metrics.stream()
            .map(Metrics::getCodeViolations)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.COMMENTS,
        metrics.stream()
            .map(Metrics::getComments)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.COMPLEXITY,
        metrics.stream()
            .map(Metrics::getComplexity)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.COVERAGE,
        metrics.stream()
            .map(Metrics::getCoverage)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.DUPLICATIONS,
        metrics.stream()
            .map(Metrics::getDuplications)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.MUTATION_TEST,
        metrics.stream()
            .map(Metrics::getMutationTest)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.RECIPE_VIOLATIONS,
        metrics.stream()
            .map(Metrics::getRecipeViolations)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    collection.put(Type.TEST,
        metrics.stream()
            .map(Metrics::getTest)
            .filter(Evaluator::isAvailable)
            .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    return this;
  }

  @Override
  public DoubleSummaryStatistics getPremirrorCache() {
    return collection.get(Type.PREMIRROR_CACHE);
  }

  @Override
  public DoubleSummaryStatistics getSharedStateCache() {
    return collection.get(Type.SHARED_STATE_CACHE);
  }

  @Override
  public DoubleSummaryStatistics getCodeViolations() {
    return collection.get(Type.CODE_VIOLATIONS);
  }

  @Override
  public DoubleSummaryStatistics getComments() {
    return collection.get(Type.COMMENTS);
  }

  @Override
  public DoubleSummaryStatistics getComplexity() {
    return collection.get(Type.COMPLEXITY);
  }

  @Override
  public DoubleSummaryStatistics getCoverage() {
    return collection.get(Type.COVERAGE);
  }

  @Override
  public DoubleSummaryStatistics getDuplications() {
    return collection.get(Type.DUPLICATIONS);
  }

  @Override
  public DoubleSummaryStatistics getMutationTest() {
    return collection.get(Type.MUTATION_TEST);
  }

  @Override
  public DoubleSummaryStatistics getRecipeViolations() {
    return collection.get(Type.RECIPE_VIOLATIONS);
  }

  @Override
  public DoubleSummaryStatistics getTest() {
    return collection.get(Type.TEST);
  }
}
