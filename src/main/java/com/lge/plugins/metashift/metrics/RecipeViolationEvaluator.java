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

import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class RecipeViolationEvaluator extends ViolationEvaluator<RecipeViolationEvaluator> {

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public RecipeViolationEvaluator(final Criteria criteria) {
    super(criteria.getRecipeViolationThreshold());
  }

  @Override
  protected void parseImpl(final Streamable c) {
    setMajor(new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(MajorRecipeViolationData.class).count()
    ));
    setMinor(new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(MinorRecipeViolationData.class).count()
    ));
    setInfo(new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(InfoRecipeViolationData.class).count()
    ));

    setAvailable(c.isAvailable(RecipeSizeData.class) && c.isAvailable(RecipeViolationData.class));
    setDenominator(c.objects(RecipeSizeData.class).mapToLong(RecipeSizeData::getLines).sum());
    setNumerator(getMajor().getNumerator() + getMinor().getNumerator() + getInfo().getNumerator());
  }
}
