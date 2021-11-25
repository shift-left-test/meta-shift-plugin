/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.ComplexityCounter;
import com.lge.plugins.metashift.analysis.ComplexityEvaluator;
import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ComplexityDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<DataSummary> {

  private final int tolerance;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public ComplexityDataSummaryAggregator(Configuration configuration) {
    super(configuration);
    tolerance = configuration.getComplexityTolerance();
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new ComplexityCounter(configuration);
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new ComplexityEvaluator(configuration);
  }

  @Override
  public List<DataSummary> parse(Recipe recipe) {
    Recipes recipes = new Recipes();
    List<CodeSizeData> files = recipe.objects(CodeSizeData.class).collect(Collectors.toList());
    for (CodeSizeData file : files) {
      DataList dataList = new DataList();
      dataList.addAll(recipe.objects(ComplexityData.class)
          .filter(o -> o.getFile().equals(file.getFile()))
          .collect(Collectors.toList()));
      if (dataList.objects(ComplexityData.class).anyMatch(o -> o.getValue() >= tolerance)) {
        dataList.add(file);
        recipes.add(new Recipe(file.getFile(), dataList));
      }
    }
    return parse(recipes);
  }
}
