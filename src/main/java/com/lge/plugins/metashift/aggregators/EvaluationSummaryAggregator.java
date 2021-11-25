/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.EvaluationSummaryCollector;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EvaluationSummaryAggregator class..
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummaryAggregator implements Aggregator<EvaluationSummary> {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public EvaluationSummaryAggregator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public List<EvaluationSummary> parse(Recipes recipes) {
    return recipes.stream()
        .map(o -> new EvaluationSummaryCollector(configuration).parse(o))
        .collect(Collectors.toList());
  }
}
