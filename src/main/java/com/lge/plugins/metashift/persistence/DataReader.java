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

package com.lge.plugins.metashift.persistence;

import java.io.Serializable;
import java.util.Optional;
import net.sf.json.JSONArray;

/**
 * DataReader class.
 *
 * @author Sung Gon Kim
 */
public class DataReader implements Archiver, Serializable {

  private static final long serialVersionUID = -4718145933099022603L;

  private final Archiver.Metric metric;
  private final DataSource dataSource;

  /**
   * Default constructor.
   *
   * @param metric     type
   * @param dataSource for persistent objects
   */
  public DataReader(Archiver.Metric metric, DataSource dataSource) {
    this.metric = metric;
    this.dataSource = dataSource;
  }

  /**
   * Returns the summary objects.
   *
   * @return summary objects
   */
  public JSONArray getSummaries() {
    JSONArray o = dataSource.get(Scope.PROJECT.name(), metric.name(), Data.SUMMARIES.name());
    return Optional.ofNullable(o).orElse(new JSONArray());
  }

  /**
   * Returns the summary objects of the recipe.
   *
   * @param recipe name
   * @return summary objects
   */
  public JSONArray getSummaries(String recipe) {
    JSONArray o = dataSource.get(Scope.RECIPE.name(), metric.name(), Data.SUMMARIES.name(), recipe);
    return Optional.ofNullable(o).orElse(new JSONArray());
  }

  /**
   * Returns the treemap objects
   *
   * @return treemap objects
   */
  public JSONArray getTreemap() {
    JSONArray o = dataSource.get(Scope.PROJECT.name(), metric.name(), Data.TREEMAP.name());
    return Optional.ofNullable(o).orElse(new JSONArray());
  }

  /**
   * Returns the segmented objects.
   *
   * @param recipe name
   * @param file   name
   * @return segmented objects
   */
  public JSONArray getObjects(String recipe, String file) {
    JSONArray o = dataSource
        .get(Scope.RECIPE.name(), metric.name(), Data.OBJECTS.name(), recipe, file);
    return Optional.ofNullable(o).orElse(new JSONArray());
  }

  /**
   * Returns the content of the file.
   *
   * @param recipe name
   * @param file   name
   * @return content of the file
   */
  public String readFile(String recipe, String file) {
    String o = dataSource.get(Scope.RECIPE.name(), Data.FILE.name(), recipe, file);
    return Optional.ofNullable(o).orElse("");
  }
}
