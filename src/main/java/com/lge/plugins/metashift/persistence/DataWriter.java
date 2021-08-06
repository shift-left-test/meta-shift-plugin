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

import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.TreemapData;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * DataWriter class.
 *
 * @author Sung Gon Kim
 */
public abstract class DataWriter implements Archiver {

  private final Archiver.Metric metric;
  private final DataSource dataSource;
  private final FilePath path;

  /**
   * Default constructor.
   *
   * @param metric     enum type
   * @param dataSource for persistent objects
   * @param path       to the report directory
   */
  public DataWriter(Metric metric, DataSource dataSource, FilePath path) {
    this.metric = metric;
    this.dataSource = dataSource;
    this.path = path;
  }

  protected Archiver.Metric getMetric() {
    return metric;
  }

  protected void put(Object object, String... names) throws IOException {
    dataSource.put(object, names);
  }

  private FilePath getFilePath(FilePath base, String prefix, String file) {
    if (new File(file).isAbsolute()) {
      return new FilePath(base.getChannel(), file);
    } else {
      return new FilePath(new FilePath(base.getChannel(), prefix), file);
    }
  }

  protected void writeFile(String recipe, String file) throws IOException, InterruptedException {
    if (dataSource.has(Scope.RECIPE.name(), Data.FILE.name(), recipe, file)) {
      return;
    }
    try {
      JSONObject metadata = JsonUtils.createObject(path.child(recipe).child("metadata.json"));
      FilePath filePath = getFilePath(path, metadata.getString("S"), file);
      dataSource.put(filePath.readToString(), Scope.RECIPE.name(), Data.FILE.name(), recipe, file);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
  }

  /**
   * Adds the treemap data objects.
   *
   * @param objects to add
   */
  public void addTreemap(List<TreemapData> objects) throws IOException {
    JSONArray array = JSONArray.fromObject(objects);
    put(array, Scope.PROJECT.name(), getMetric().name(), Data.TREEMAP.name());
  }

  protected abstract JSONObject toJsonObject(DataSummary summary);

  /**
   * Adds the data summaries.
   *
   * @param summaries to add
   */
  public void addSummaries(List<DataSummary> summaries) throws IOException {
    JSONArray array = new JSONArray();
    summaries.forEach(o -> array.add(toJsonObject(o)));
    put(array, Scope.PROJECT.name(), getMetric().name(), Data.SUMMARIES.name());
  }
}
