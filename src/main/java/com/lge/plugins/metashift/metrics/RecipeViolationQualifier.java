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

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.RecipeViolationList;

/**
 * Evaluates the level of recipe violations.
 *
 * @author Sung Gon Kim
 */
public final class RecipeViolationQualifier extends Qualifier<RecipeViolationCounter> {

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public RecipeViolationQualifier(final float threshold) {
    super(threshold,
        new MajorRecipeViolationCounter(),
        new MinorRecipeViolationCounter(),
        new InfoRecipeViolationCounter());
  }

  @Override
  public int getDenominator() {
    return get(MajorRecipeViolationCounter.class).getDenominator();
  }

  @Override
  public int getNumerator() {
    return get(MajorRecipeViolationCounter.class).getNumerator();
  }

  @Override
  public boolean isQualified() {
    if (getDenominator() == 0) {
      return false;
    }
    return ((float) getNumerator() / (float) getDenominator()) <= getThreshold();
  }

  @Override
  public void visit(final RecipeViolationList objects) {
    getCollection().values().forEach(objects::accept);
  }
}
