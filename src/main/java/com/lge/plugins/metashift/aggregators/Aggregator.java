/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.models.Recipes;
import java.util.List;

/**
 * Aggregator interface.
 *
 * @param <R> object type
 * @author Sung Gon Kim
 */
public interface Aggregator<R> {

  /**
   * Parses the recipes to create the list of results.
   *
   * @param recipes to parse
   * @return the list of results
   */
  List<R> parse(Recipes recipes);
}
