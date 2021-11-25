/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Streamable;

/**
 * BranchCoverageCounter class.
 *
 * @author Sung Gon Kim
 */
public class BranchCoverageCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long covered = s.objects(BranchCoverageData.class).filter(CoverageData::isCovered).count();
    long uncovered = s.objects(BranchCoverageData.class).filter(o -> !o.isCovered()).count();
    return new Distribution(covered, uncovered);
  }
}
