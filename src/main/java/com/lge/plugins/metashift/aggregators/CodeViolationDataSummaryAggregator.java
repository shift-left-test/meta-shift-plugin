/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.CodeViolationCounter;
import com.lge.plugins.metashift.analysis.CodeViolationEvaluator;
import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CodeViolationDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<DataSummary> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public CodeViolationDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new CodeViolationCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new CodeViolationEvaluator(configuration);
  }

  @Override
  public List<DataSummary> parse(Recipe recipe) {
    Recipes recipes = new Recipes();
    List<CodeSizeData> files = recipe.objects(CodeSizeData.class).collect(Collectors.toList());
    for (CodeSizeData file : files) {
      DataList dataList = new DataList();
      dataList.add(file);
      dataList.addAll(recipe.objects(CodeViolationData.class)
          .filter(o -> o.getFile().equals(file.getFile()))
          .collect(Collectors.toList()));
      recipes.add(new Recipe(file.getFile(), dataList));
    }
    return parse(recipes);
  }
}
