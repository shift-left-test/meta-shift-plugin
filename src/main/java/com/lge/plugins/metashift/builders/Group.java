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
import java.io.Serializable;
import java.util.Optional;

/**
 * Group class.
 *
 * @author Sung Gon Kim
 */
public class Group implements Serializable {

  private static final long serialVersionUID = 802493641209916939L;

  private final DataSource dataSource;
  private final Scope scope;
  private final Metric metric;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   * @param scope      type
   * @param metric     type
   */
  public Group(DataSource dataSource, Scope scope, Metric metric) {
    this.dataSource = dataSource;
    this.scope = scope;
    this.metric = metric;
  }

  private <T> T getOrDefault(T defaultValue, String... names) {
    T o = dataSource.get(names);
    return Optional.ofNullable(o).orElse(defaultValue);
  }

  protected <T> T getOrDefault(Scope scope, Data data, T defaultValue) {
    return getOrDefault(defaultValue, scope.name(), metric.name(), data.name());
  }

  protected <T> T getOrDefault(Data data, T defaultValue) {
    return getOrDefault(defaultValue, scope.name(), metric.name(), data.name());
  }

  protected <T> T getOrDefault(Data data, String recipe, T defaultValue) {
    return getOrDefault(defaultValue, scope.name(), metric.name(), data.name(), recipe);
  }

  protected <T> T getOrDefault(Data data, String recipe, String file, T defaultValue) {
    return getOrDefault(defaultValue, scope.name(), metric.name(), data.name(), recipe, file);
  }

  protected String readFile(String recipe, String file) {
    return getOrDefault("", scope.name(), Metric.NONE.name(), Data.FILE.name(), recipe, file);
  }
}
