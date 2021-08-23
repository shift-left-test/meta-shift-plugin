/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
      return new PositiveTreemapData(recipe.getName(), linesOfCode, 0, threshold, ratio);
    } else {
      return new NegativeTreemapData(recipe.getName(), linesOfCode, threshold, max, ratio);
    }
  }
}
