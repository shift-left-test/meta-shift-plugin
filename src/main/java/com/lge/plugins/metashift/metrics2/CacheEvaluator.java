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

package com.lge.plugins.metashift.metrics2;

import com.lge.plugins.metashift.models.CacheData;
import com.lge.plugins.metashift.models.Collectable;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * CacheEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class CacheEvaluator extends Evaluator<CacheEvaluator> {

  /**
   * Represents the type of caches.
   */
  private enum Type {
    /**
     * Premirror cache.
     */
    PREMIRROR,

    /**
     * Shared state cache.
     */
    SHARED_STATE,
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
  public CacheEvaluator(final Criteria criteria) {
    super(criteria.getCacheThreshold());
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
  }

  /**
   * Returns the premirror counter object.
   *
   * @return premirror counter object
   */
  public Counter getPremirror() {
    return collection.get(Type.PREMIRROR);
  }

  /**
   * Returns the shared state counter object.
   *
   * @return shared state counter object
   */
  public Counter getSharedState() {
    return collection.get(Type.SHARED_STATE);
  }

  @Override
  protected void parseImpl(final Collectable c) {
    collection.put(Type.PREMIRROR, new Counter(
        c.objects(PremirrorCacheData.class).distinct().count(),
        c.objects(PremirrorCacheData.class).distinct().filter(CacheData::isAvailable).count()
    ));
    collection.put(Type.SHARED_STATE, new Counter(
        c.objects(SharedStateCacheData.class).distinct().count(),
        c.objects(SharedStateCacheData.class).distinct().filter(CacheData::isAvailable).count()
    ));

    setDenominator(collection.values().stream().mapToLong(Counter::getDenominator).sum());
    setNumerator(collection.values().stream().mapToLong(Counter::getNumerator).sum());
  }
}
