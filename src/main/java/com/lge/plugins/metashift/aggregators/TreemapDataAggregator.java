/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeTreemapData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.PositiveTreemapData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.TreemapData;
import java.util.ArrayList;
import java.util.List;

/**
 * TreemapDataAggregator class.
 *
 * @author Sung Gon Kim
 */
public class TreemapDataAggregator implements Aggregator<TreemapData> {

  private final Evaluator evaluator;

  /**
   * Default constructor.
   *
   * @param evaluator for evaluation
   */
  public TreemapDataAggregator(Evaluator evaluator) {
    this.evaluator = evaluator;
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
    long linesOfCode = new LinesOfCodeCollector().parse(recipe).getLines();
    Evaluation evaluation = evaluator.parse(recipe);
    double threshold = evaluation.getThreshold();
    double ratio = evaluation.getRatio();
    if (evaluation instanceof PositiveEvaluation) {
      return new PositiveTreemapData(recipe.getName(), linesOfCode, threshold, ratio);
    } else {
      return new NegativeTreemapData(recipe.getName(), linesOfCode, threshold, max, ratio);
    }
  }
}
