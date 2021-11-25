/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.DuplicationCounter;
import com.lge.plugins.metashift.analysis.DuplicationEvaluator;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DuplicationDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<DataSummary> {

  private final int tolerance;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public DuplicationDataSummaryAggregator(Configuration configuration) {
    super(configuration);
    tolerance = configuration.getDuplicationTolerance();
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new DuplicationCounter(configuration);
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new DuplicationEvaluator(configuration);
  }

  @Override
  public List<DataSummary> parse(Recipe recipe) {
    Recipes recipes = new Recipes();
    List<CodeSizeData> files = recipe.objects(CodeSizeData.class).collect(Collectors.toList());
    for (CodeSizeData file : files) {
      DataList dataList = new DataList();
      dataList.addAll(recipe.objects(DuplicationData.class)
          .filter(o -> o.getFile().equals(file.getFile()))
          .collect(Collectors.toList()));
      if (dataList.objects(DuplicationData.class)
          .anyMatch(o -> o.getDuplicatedLines() >= tolerance)) {
        dataList.add(file);
        recipes.add(new Recipe(file.getFile(), dataList));
      }
    }
    return parse(recipes);
  }
}
