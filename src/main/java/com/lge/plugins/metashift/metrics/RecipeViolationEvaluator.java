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

import com.lge.plugins.metashift.models.Collectable;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.RecipeViolationData;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class RecipeViolationEvaluator extends Evaluator<RecipeViolationEvaluator> {

  /**
   * Represents the recipe violation types.
   */
  private enum Type {
    /**
     * Major violations.
     */
    MAJOR,

    /**
     * Minor violations.
     */
    MINOR,

    /**
     * Info violations.
     */
    INFO,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, Counter> collection;

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public RecipeViolationEvaluator(final Criteria criteria) {
    super(criteria.getRecipeViolationThreshold());
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
  }

  @Override
  public boolean isQualified() {
    return isAvailable() && (double) getNumerator() / (double) getDenominator() <= getThreshold();
  }

  /**
   * Returns the major violation counter.
   *
   * @return counter object
   */
  public Counter getMajor() {
    return collection.get(Type.MAJOR);
  }

  /**
   * Returns the minor violation counter.
   *
   * @return counter object
   */
  public Counter getMinor() {
    return collection.get(Type.MINOR);
  }

  /**
   * Returns the info violation counter.
   *
   * @return counter object
   */
  public Counter getInfo() {
    return collection.get(Type.INFO);
  }

  @Override
  protected void parseImpl(final Collectable c) {
    collection.put(Type.MAJOR, new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(MajorRecipeViolationData.class).count()
    ));
    collection.put(Type.MINOR, new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(MinorRecipeViolationData.class).count()
    ));
    collection.put(Type.INFO, new Counter(
        c.objects(RecipeViolationData.class).count(),
        c.objects(InfoRecipeViolationData.class).count()
    ));

    setDenominator(getMajor().getDenominator());
    setNumerator(getMajor().getNumerator());
  }
}
