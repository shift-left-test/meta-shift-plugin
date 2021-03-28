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

import java.util.HashMap;
import java.util.Map;

/**
 * Qualifier class.
 *
 * @author Sung Gon Kim
 */
public abstract class Qualifier<T> implements Visitable {

  /**
   * Represents the collection of objects.
   */
  private final Map<Class<?>, T> collection;

  /**
   * Represents the threshold of the qualification.
   */
  private final float threshold;

  /**
   * Default constructor.
   *
   * @param threshold for qualification
   * @param args      of initialization list
   */
  @SafeVarargs
  public Qualifier(final float threshold, T... args) {
    this.threshold = threshold;
    collection = new HashMap<>();
    for (T arg : args) {
      collection.put(arg.getClass(), arg);
    }
  }

  /**
   * Returns the relevant object from the collection.
   *
   * @param clazz the name of the class
   * @return relevant object
   */
  public final T get(final Class<? extends T> clazz) {
    return clazz.cast(collection.get(clazz));
  }

  /**
   * Returns the collection.
   *
   * @return collection object
   */
  public final Map<Class<?>, T> getCollection() {
    return collection;
  }

  /**
   * Returns the threshold.
   *
   * @return threshold value
   */
  public final float getThreshold() {
    return threshold;
  }

  /**
   * Returns whether the metric is available.
   *
   * @return true if the metric is available, false otherwise
   */
  public abstract boolean isAvailable();

  /**
   * Returns whether the evaluation of the metric meets the criteria.
   *
   * @return true if the metric meets the criteria, false otherwise
   */
  public abstract boolean isQualified();
}
