/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.analysis.PremirrorCacheCounter;
import com.lge.plugins.metashift.analysis.PremirrorCacheEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The PremirrorCacheDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<PremirrorCacheData> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public PremirrorCacheDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new PremirrorCacheCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new PremirrorCacheEvaluator(configuration);
  }

  @Override
  public List<PremirrorCacheData> parse(Recipe recipe) {
    return recipe.objects(PremirrorCacheData.class).distinct().collect(Collectors.toList());
  }
}
