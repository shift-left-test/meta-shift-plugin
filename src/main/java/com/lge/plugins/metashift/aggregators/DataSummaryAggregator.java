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

import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public abstract class DataSummaryAggregator implements Aggregator<DataSummary> {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public DataSummaryAggregator(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Returns the lines of code object.
   *
   * @return lines of code object
   */
  protected abstract LinesOfCode getLinesOfCode(Recipe recipe);

  /**
   * Returns the counter object.
   *
   * @param configuration for evaluation
   * @return Counter object
   */
  protected abstract Counter getCounter(Configuration configuration);

  /**
   * Returns the evaluator object.
   *
   * @param configuration for evaluation
   * @return Evaluator object
   */
  protected abstract Evaluator getEvaluator(Configuration configuration);

  private DataSummary newDataSummary(Recipe recipe) {
    return new DataSummary(
        recipe.getName(),
        getLinesOfCode(recipe),
        getCounter(configuration).parse(recipe),
        getEvaluator(configuration).parse(recipe)
    );
  }

  @Override
  public List<DataSummary> parse(Recipes recipes) {
    return recipes.stream()
        .filter(o -> getEvaluator(configuration).parse(o).isAvailable())
        .map(this::newDataSummary)
        .collect(Collectors.toList());
  }
}
