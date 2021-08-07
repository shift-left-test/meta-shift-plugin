/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
