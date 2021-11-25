/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Streamable;

/**
 * DuplicationCounter class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationCounter implements Counter {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration object
   */
  public DuplicationCounter(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Distribution parse(Streamable s) {
    DuplicationCalculator calculator = new DuplicationCalculator(configuration).parse(s);
    return new Distribution(calculator.getDuplicateLines(), calculator.getUniqueLines());
  }
}
