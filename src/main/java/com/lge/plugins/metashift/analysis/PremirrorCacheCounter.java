/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * PremirrorCacheCounter class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long hits = s.objects(PremirrorCacheData.class).distinct()
        .filter(PremirrorCacheData::isAvailable).count();
    long misses = s.objects(PremirrorCacheData.class).distinct()
        .filter(o -> !o.isAvailable()).count();
    return new Distribution(hits, misses);
  }
}
