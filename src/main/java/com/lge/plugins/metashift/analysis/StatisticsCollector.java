/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatisticsCollector class.
 *
 * @author Sung Gon Kim
 */
public class StatisticsCollector implements Collector<Recipes, Statistics> {

  private final Evaluator evaluator;

  public StatisticsCollector(Evaluator evaluator) {
    this.evaluator = evaluator;
  }

  @Override
  public Statistics parse(Recipes recipes) {
    List<Evaluation> evaluations = recipes.stream()
        .map(evaluator::parse).collect(Collectors.toList());
    DoubleSummaryStatistics statistics = evaluations.stream()
        .filter(Evaluation::isAvailable)
        .collect(Collectors.summarizingDouble(Evaluation::getRatio));
    return new Statistics(statistics);
  }
}
