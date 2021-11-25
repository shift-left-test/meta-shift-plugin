/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.analysis.SharedStateCacheCounter;
import com.lge.plugins.metashift.analysis.SharedStateCacheEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SharedStateCacheDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<SharedStateCacheData> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public SharedStateCacheDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new SharedStateCacheCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new SharedStateCacheEvaluator(configuration);
  }

  @Override
  public List<SharedStateCacheData> parse(Recipe recipe) {
    return recipe.objects(SharedStateCacheData.class).distinct().collect(Collectors.toList());
  }
}
