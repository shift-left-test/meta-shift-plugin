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

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public RecipeViolationEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available =
        s.isAvailable(RecipeSizeData.class) && s.isAvailable(RecipeViolationData.class);
    long denominator = s.objects(RecipeSizeData.class).mapToLong(RecipeSizeData::getLines).sum();
    long numerator = s.objects(RecipeViolationData.class).count();
    double threshold = configuration.getRecipeViolationThreshold();
    return new NegativeEvaluation(available, denominator, numerator, threshold);
  }
}
