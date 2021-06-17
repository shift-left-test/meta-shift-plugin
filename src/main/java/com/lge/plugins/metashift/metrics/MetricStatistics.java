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

import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.Recipes;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the group of statistics for each metric.
 *
 * @author Sung Gon Kim
 */
public class MetricStatistics extends GroupParser<DoubleSummaryStatistics> {

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
    super(new DoubleSummaryStatistics());
    this.criteria = criteria;
  }

  @Override
  public void parse(final Recipes recipes) {
    List<Metrics> metrics = recipes.stream()
        .map(recipe -> new Metrics(criteria).parse(recipe))
        .collect(Collectors.toList());
    parse(metrics);
  }

  @Override
  public void parse(final List<Metrics> metrics) {
    setPremirrorCache(metrics.stream()
        .map(Metrics::getPremirrorCache)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setSharedStateCache(metrics.stream()
        .map(Metrics::getSharedStateCache)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setCodeViolations(metrics.stream()
        .map(Metrics::getCodeViolations)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setComments(metrics.stream()
        .map(Metrics::getComments)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setComplexity(metrics.stream()
        .map(Metrics::getComplexity)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setCoverage(metrics.stream()
        .map(Metrics::getCoverage)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setDuplications(metrics.stream()
        .map(Metrics::getDuplications)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setMutationTest(metrics.stream()
        .map(Metrics::getMutationTest)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setRecipeViolations(metrics.stream()
        .map(Metrics::getRecipeViolations)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));

    setTest(metrics.stream()
        .map(Metrics::getTest)
        .filter(Evaluator::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluator::getRatio)));
  }
}