/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * StatementCoverageCounter class.
 *
 * @author Sung Gon Kim
 */
public class StatementCoverageCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long covered = s.objects(StatementCoverageData.class).filter(CoverageData::isCovered).count();
    long uncovered = s.objects(StatementCoverageData.class).filter(o -> !o.isCovered()).count();
    return new Distribution(covered, uncovered);
  }
}
