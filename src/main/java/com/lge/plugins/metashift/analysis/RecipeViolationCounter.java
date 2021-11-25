/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * RecipeViolationCounter class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long major = s.objects(MajorRecipeViolationData.class).count();
    long minor = s.objects(MinorRecipeViolationData.class).count();
    long info = s.objects(InfoRecipeViolationData.class).count();
    return new Distribution(major, minor, info);
  }
}
