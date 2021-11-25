/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.analysis.MutationTestCounter;
import com.lge.plugins.metashift.analysis.MutationTestEvaluator;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MutationTestDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestDataSummaryAggregator
    extends DataSummaryAggregator implements RecipeAggregator<DataSummary> {

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public MutationTestDataSummaryAggregator(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected LinesOfCode getLinesOfCode(Recipe recipe) {
    return new LinesOfCodeCollector().parse(recipe);
  }

  @Override
  protected Counter getCounter(Configuration configuration) {
    return new MutationTestCounter();
  }

  @Override
  protected Evaluator getEvaluator(Configuration configuration) {
    return new MutationTestEvaluator(configuration);
  }

  @Override
  public List<DataSummary> parse(Recipe recipe) {
    Recipes recipes = new Recipes();
    List<String> files = recipe.objects(MutationTestData.class)
        .map(MutationTestData::getFile)
        .distinct()
        .collect(Collectors.toList());
    for (String file : files) {
      DataList dataList = new DataList();
      dataList.addAll(recipe.objects(CodeSizeData.class)
          .filter(o -> o.getFile().equals(file))
          .collect(Collectors.toList()));
      dataList.addAll(recipe.objects(MutationTestData.class)
          .filter(o -> o.getFile().equals(file))
          .collect(Collectors.toList()));
      recipes.add(new Recipe(file, dataList));
    }
    return parse(recipes);
  }
}
