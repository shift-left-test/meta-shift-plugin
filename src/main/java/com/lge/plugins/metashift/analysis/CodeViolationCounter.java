/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CodeViolationCounter class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long major = s.objects(MajorCodeViolationData.class).count();
    long minor = s.objects(MinorCodeViolationData.class).count();
    long info = s.objects(InfoCodeViolationData.class).count();
    return new Distribution(major, minor, info);
  }
}
