/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;

/**
 * MutationTestCounter class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long killed = s.objects(KilledMutationTestData.class).count();
    long survived = s.objects(SurvivedMutationTestData.class).count();
    long skipped = s.objects(SkippedMutationTestData.class).count();
    return new Distribution(killed, survived, skipped);
  }
}
