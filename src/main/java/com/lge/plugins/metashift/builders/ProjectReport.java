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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ProjectReport class.
 *
 * @author Sung Gon Kim
 */
public class ProjectReport extends Report<ProjectGroup> {

  private static final long serialVersionUID = 7057813789045080156L;

  private final DataSource dataSource;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   */
  public ProjectReport(DataSource dataSource) {
    this.dataSource = dataSource;
    Stream.of(Metric.values()).forEach(o -> put(o, new ProjectGroup(dataSource, o)));
  }

  private <T> T getOrDefault(Metric metric, Data data, T defaultValue) {
    T o = dataSource.get(Scope.PROJECT.name(), metric.name(), data.name());
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

  /**
   * Returns the tested recipe evaluation result.
   *
   * @return evaluation result
   */
  public JSONObject getTestedRecipes() {
    return getOrDefault(Metric.TESTED_RECIPES, Data.EVALUATION, new JSONObject());
  }

  /**
   * Returns the treemap objects.
   *
   * @return treemap objects
   */
  public JSONArray getTreemap() {
    return getOrDefault(Metric.NONE, Data.TREEMAP, new JSONArray());
  }

  /**
   * Returns the summary objects.
   *
   * @return summary objects
   */
  public JSONArray getSummaries() {
    return getOrDefault(Metric.NONE, Data.SUMMARIES, new JSONArray());
  }
}
