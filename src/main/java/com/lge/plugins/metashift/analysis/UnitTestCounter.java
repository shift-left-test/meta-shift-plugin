/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * UnitTestCounter class.
 *
 * @author Sung Gon Kim
 */
public class UnitTestCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long passed = s.objects(PassedTestData.class).count();
    long failed = s.objects(FailedTestData.class).count();
    long error = s.objects(ErrorTestData.class).count();
    long skipped = s.objects(SkippedTestData.class).count();
    return new Distribution(passed, failed, error, skipped);
  }
}
