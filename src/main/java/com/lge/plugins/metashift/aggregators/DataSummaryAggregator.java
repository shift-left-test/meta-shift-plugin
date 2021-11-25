/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public abstract class DataSummaryAggregator implements Aggregator<DataSummary> {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public DataSummaryAggregator(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Returns the lines of code object.
   *
   * @return lines of code object
   */
  protected abstract LinesOfCode getLinesOfCode(Recipe recipe);

  /**
   * Returns the counter object.
   *
   * @param configuration for evaluation
   * @return Counter object
   */
  protected abstract Counter getCounter(Configuration configuration);

  /**
   * Returns the evaluator object.
   *
   * @param configuration for evaluation
   * @return Evaluator object
   */
  protected abstract Evaluator getEvaluator(Configuration configuration);

  private DataSummary newDataSummary(Recipe recipe) {
    return new DataSummary(
        recipe.getName(),
        getLinesOfCode(recipe),
        getCounter(configuration).parse(recipe),
        getEvaluator(configuration).parse(recipe)
    );
  }

  @Override
  public List<DataSummary> parse(Recipes recipes) {
    return recipes.stream()
        .filter(o -> getEvaluator(configuration).parse(o).isAvailable())
        .map(this::newDataSummary)
        .collect(Collectors.toList());
  }
}
