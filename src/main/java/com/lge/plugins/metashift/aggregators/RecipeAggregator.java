/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import com.lge.plugins.metashift.models.Recipe;
import java.util.List;

/**
 * RecipeAggregator interface.
 *
 * @param <R> object type
 * @author Sung Gon Kim
 */
public interface RecipeAggregator<R> {

  /**
   * Parses the recipe to create the list of results.
   *
   * @param recipe to parse
   * @return the list of results
   */
  List<R> parse(Recipe recipe);
}
