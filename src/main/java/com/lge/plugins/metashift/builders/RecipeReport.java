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
