/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.RecipeViolationCounter;
import com.lge.plugins.metashift.analysis.RecipeViolationEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RecipeViolationDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<DataSummary> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public RecipeViolationDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    long lines = recipe.objects(RecipeSizeData.class).mapToLong(RecipeSizeData::getLines).sum();
    return new LinesOfCode(lines, 0, 0, 0, 0);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new RecipeViolationCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new RecipeViolationEvaluator(configuration);
  }

  @Override
  public List<DataSummary> parse(Recipe recipe) {
    Recipes recipes = new Recipes();
    List<RecipeSizeData> files = recipe.objects(RecipeSizeData.class).collect(Collectors.toList());
    for (RecipeSizeData file : files) {
      DataList dataList = new DataList();
      dataList.add(file);
      dataList.addAll(recipe.objects(RecipeViolationData.class)
          .filter(o -> o.getFile().equals(file.getFile()))
          .collect(Collectors.toList()));
      recipes.add(new Recipe(file.getFile(), dataList));
    }
    return parse(recipes);
  }
}
