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

package com.lge.plugins.metashift.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates the level of recipe violations.
 *
 * @author Sung Gon Kim
 */
public final class RecipeViolationQualifier extends Visitor implements Qualifier {
  /**
   * Represents the collection of RecipeViolationCounter objects.
   */
  private Map<Class<? extends RecipeViolationData>, RecipeViolationCounter> collection;
  /**
   * Represents the threshold of the qualification.
   */
  private float threshold;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public RecipeViolationQualifier(final float threshold) {
    collection = new HashMap<>();
    collection.put(MajorRecipeViolationData.class,
                   new RecipeViolationCounter(
                       MajorRecipeViolationData.class));
    collection.put(MinorRecipeViolationData.class,
                   new RecipeViolationCounter(
                       MinorRecipeViolationData.class));
    collection.put(InfoRecipeViolationData.class,
                   new RecipeViolationCounter(
                       InfoRecipeViolationData.class));
    this.threshold = threshold;
  }

  /**
   * Returns the relevant collector object based on the given type.
   *
   * @param clazz of the object type to return
   * @return RecipeViolationCounter object
   */
  public RecipeViolationCounter collection(final Class<? extends RecipeViolationData> clazz) {
    return collection.get(clazz);
  }

  @Override
  public boolean isAvailable() {
    return collection(MajorRecipeViolationData.class).getDenominator() > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = collection(MajorRecipeViolationData.class).getDenominator();
    int numerator = collection(MajorRecipeViolationData.class).getNumerator();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) <= threshold;
  }

  @Override
  public void visit(final RecipeViolationSet objects) {
    collection.values().stream().forEach(collector -> objects.accept(collector));
  }
}
