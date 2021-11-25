/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Streamable;

/**
 * ComplexityCounter class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityCounter implements Counter {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration object
   */
  public ComplexityCounter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Distribution parse(Streamable s) {
    long tolerance = configuration.getComplexityTolerance();
    long abnormal = s.objects(ComplexityData.class).filter(o -> o.getValue() >= tolerance).count();
    long normal = s.objects(ComplexityData.class).filter(o -> o.getValue() < tolerance).count();
    return new Distribution(abnormal, normal);
  }
}
