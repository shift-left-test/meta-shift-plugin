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

import com.lge.plugins.metashift.models.CacheData;
import com.lge.plugins.metashift.models.CacheList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collects the cache availability information from the given data sets.
 *
 * @author Sung Gon Kim
 */
public abstract class CacheCounter implements Countable {

  /**
   * Represents the unique set of cache objects.
   */
  private Set<CacheData> caches;

  /**
   * Represents the class type.
   */
  private final Class<? extends CacheData> clazz;

  /**
   * Default constructor.
   *
   * @param clazz the class type
   */
  public CacheCounter(final Class<? extends CacheData> clazz) {
    this.caches = new HashSet<>();
    this.clazz = clazz;
  }

  @Override
  public int getDenominator() {
    return caches.size();
  }

  @Override
  public int getNumerator() {
    return Long.valueOf(caches.stream().filter(CacheData::isAvailable).count()).intValue();
  }

  @Override
  public void visit(final CacheList objects) {
    caches.addAll(objects.stream().filter(o -> o.getClass() == clazz)
        .collect(Collectors.toList()));
  }
}
