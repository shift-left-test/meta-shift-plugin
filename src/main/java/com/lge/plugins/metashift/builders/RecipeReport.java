/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.persistence.DataSource;
import java.util.Optional;
import java.util.stream.Stream;
import net.sf.json.JSONObject;

/**
 * RecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class RecipeReport extends Report<RecipeGroup> {

  private static final long serialVersionUID = 6265637139439193655L;

  private final DataSource dataSource;
  private final String recipe;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   * @param recipe     name
   */
  public RecipeReport(DataSource dataSource, String recipe) {
    this.dataSource = dataSource;
    this.recipe = recipe;
    Stream.of(Metric.values()).forEach(o -> put(o, new RecipeGroup(dataSource, o, recipe)));
  }

  private <T> T getOrDefault(Metric metric, Data data, T defaultValue) {
    T o = dataSource.get(Scope.RECIPE.name(), metric.name(), data.name(), recipe);
    return Optional.ofNullable(o).orElse(defaultValue);
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public JSONObject getLinesOfCode() {
    return getOrDefault(Metric.NONE, Data.LINES_OF_CODE, new JSONObject());
  }
}
