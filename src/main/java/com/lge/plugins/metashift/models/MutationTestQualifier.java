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
 * Evaluates the level of mutation testing.
 *
 * @author Sung Gon Kim
 */
public final class MutationTestQualifier extends Visitor implements Qualifier {
  /**
   * Represents the collection of MutationTestCounter objects.
   */
  private Map<Class<? extends MutationTestData>, MutationTestCounter> collection;
  /**
   * Represents the threshold of the qualification.
   */
  private float threshold;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public MutationTestQualifier(final float threshold) {
    collection = new HashMap<>();
    collection.put(KilledMutationTestData.class,
                   new MutationTestCounter(KilledMutationTestData.class));
    collection.put(SurvivedMutationTestData.class,
                   new MutationTestCounter(SurvivedMutationTestData.class));
    this.threshold = threshold;
  }

  /**
   * Returns the relevant collector object based on the given type.
   *
   * @param clazz of the object type to return
   * @return MutationTestCounter object
   */
  public MutationTestCounter collection(final Class<? extends MutationTestData> clazz) {
    return collection.get(clazz);
  }

  @Override
  public boolean isAvailable() {
    return collection(KilledMutationTestData.class).getDenominator() > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = collection(KilledMutationTestData.class).getDenominator();
    int numerator = collection(KilledMutationTestData.class).getNumerator();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) >= threshold;
  }

  @Override
  public void visit(MutationTestSet objects) {
    collection.values().stream().forEach(collector -> objects.accept(collector));
  }
}
