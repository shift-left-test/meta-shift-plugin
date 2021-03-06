/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.analysis.UnitTestCounter;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.TestData;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UnitTestDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class UnitTestDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<TestData> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public UnitTestDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new UnitTestCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new UnitTestEvaluator(configuration);
  }

  @Override
  public List<TestData> parse(Recipe recipe) {
    return recipe.objects(TestData.class).collect(Collectors.toList());
  }
}
