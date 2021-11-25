/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.persistence.DataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ProjectGroup class.
 *
 * @author Sung Gon Kim
 */
public class ProjectGroup extends Group {

  private static final long serialVersionUID = -8031715446116853566L;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   * @param metric     type
   */
  public ProjectGroup(DataSource dataSource, Metric metric) {
    super(dataSource, Scope.PROJECT, metric);
  }

  /**
   * Returns the evaluation result.
   *
   * @return evaluation result
   */
  public JSONObject getEvaluation() {
    return getOrDefault(Data.EVALUATION, new JSONObject());
  }

  /**
   * Returns the statistics result.
   *
   * @return statistics result
   */
  public JSONObject getStatistics() {
    return getOrDefault(Data.STATISTICS, new JSONObject());
  }

  /**
   * Returns the distribution result.
   *
   * @return distribution result
   */
  public JSONObject getDistribution() {
    return getOrDefault(Data.DISTRIBUTION, new JSONObject());
  }

  /**
   * Returns the treemap objects.
   *
   * @return treemap objects
   */
  public JSONArray getTreemap() {
    return getOrDefault(Data.TREEMAP, new JSONArray());
  }

  /**
   * Returns the summary objects.
   *
   * @return summary objects
   */
  public JSONArray getSummaries() {
    return getOrDefault(Data.SUMMARIES, new JSONArray());
  }
}
