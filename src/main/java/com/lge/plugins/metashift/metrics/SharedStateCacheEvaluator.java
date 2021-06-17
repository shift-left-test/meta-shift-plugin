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
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * SharedStateCacheEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheEvaluator extends PositiveEvaluator<SharedStateCacheEvaluator> {

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public SharedStateCacheEvaluator(final Criteria criteria) {
    super((double) criteria.getSharedStateCacheThreshold() / 100.0);
  }

  @Override
  protected void parseImpl(final Streamable c) {
    setAvailable(c.isAvailable(SharedStateCacheData.class));
    setDenominator(c.objects(SharedStateCacheData.class).distinct().count());
    setNumerator(c.objects(SharedStateCacheData.class).distinct()
        .filter(SharedStateCacheData::isAvailable).count());
  }
}
