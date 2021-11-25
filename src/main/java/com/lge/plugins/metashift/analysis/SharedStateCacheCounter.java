/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * SharedStateCacheCounter class.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long hits = s.objects(SharedStateCacheData.class).distinct()
        .filter(SharedStateCacheData::isAvailable).count();
    long misses = s.objects(SharedStateCacheData.class).distinct()
        .filter(o -> !o.isAvailable()).count();
    return new Distribution(hits, misses);
  }
}
