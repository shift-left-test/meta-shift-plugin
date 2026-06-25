/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeTreemapData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.PositiveTreemapData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.TreemapData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;

/**
 * TreemapDataAggregator class.
 *
 * @author Sung Gon Kim
 */
public class TreemapDataAggregator implements Aggregator<TreemapData> {

  private final Evaluator evaluator;
  private final ToLongFunction<Recipe> sizeFunction;

  /**
   * Default constructor.
   *
   * @param evaluator    for evaluation
   * @param sizeFunction function returning box size for each recipe
   */
  public TreemapDataAggregator(Evaluator evaluator, ToLongFunction<Recipe> sizeFunction) {
    this.evaluator = evaluator;
    this.sizeFunction = sizeFunction;
  }

  @Override
  public List<TreemapData> parse(Recipes recipes) {
    double max = recipes.stream().mapToDouble(o -> evaluator.parse(o).getRatio()).max().orElse(0.0);
    List<TreemapData> objects = new ArrayList<>();
    recipes.stream()
        .filter(o -> evaluator.parse(o).isAvailable())
        .forEach(o -> objects.add(newTreeMapData(o, max)));
    return objects;
  }

  private TreemapData newTreeMapData(Recipe recipe, double max) {
    long size = sizeFunction.applyAsLong(recipe);
    Evaluation evaluation = evaluator.parse(recipe);
    double threshold = evaluation.getThreshold();
    double ratio = evaluation.getRatio();
    if (evaluation instanceof PositiveEvaluation) {
      return new PositiveTreemapData(recipe.getName(), size, threshold, ratio);
    } else {
      return new NegativeTreemapData(recipe.getName(), size, threshold, max, ratio);
    }
  }
}
