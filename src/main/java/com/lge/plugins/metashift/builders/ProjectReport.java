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
