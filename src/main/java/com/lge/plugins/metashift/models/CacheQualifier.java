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

import java.util.*;

/**
 * Evaluates the level of cache availability
 *
 * @author Sung Gon Kim
 */
public class CacheQualifier extends Visitor implements Qualifiable {
  private Map<Class<? extends CacheData>, CacheCollector> collection;
  private float threshold;

  /**
   * Default constructor
   *
   * @param threshold for evaluation
   */
  public CacheQualifier(float threshold) {
    this.collection = new HashMap<>();
    this.collection.put(PremirrorCacheData.class, new CacheCollector(PremirrorCacheData.class));
    this.collection.put(SharedStateCacheData.class, new CacheCollector(SharedStateCacheData.class));
    this.threshold = threshold;
  }

  /**
   * Returns the relevant CacheCollector object based on the given class type
   *
   * @param clazz of the object to return
   * @return CacheCollector object
   */
  public CacheCollector collection(Class<? extends CacheData> clazz) {
    return collection.get(clazz);
  }

  @Override
  public boolean isAvailable() {
    return collection.values().stream().mapToInt(CacheCollector::getDenominator).sum() > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = collection.values().stream().mapToInt(CacheCollector::getDenominator).sum();
    int numerator = collection.values().stream().mapToInt(CacheCollector::getNumerator).sum();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) >= threshold;
  }

  @Override
  public void visit(CacheSet objects) {
    collection.values().stream().forEach(collector -> objects.accept(collector));
  }
}
