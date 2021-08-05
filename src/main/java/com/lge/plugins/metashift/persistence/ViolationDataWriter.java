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
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.ViolationData;
import hudson.FilePath;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ViolationDataWriter class.
 *
 * @author Sung Gon Kim
 */
public abstract class ViolationDataWriter extends DataWriter {

  private final Class<? extends ViolationData> clazz;

  /**
   * Default constructor.
   *
   * @param metric     type
   * @param dataSource for persistent objects
   * @param path       to the report directory
   * @param clazz      object type
   */
  public ViolationDataWriter(Metric metric, DataSource dataSource, FilePath path,
      Class<? extends ViolationData> clazz) {
    super(metric, dataSource, path);
    this.clazz = clazz;
  }

  @Override
  protected JSONObject toJsonObject(DataSummary summary) {
    JSONObject o = new JSONObject();
    o.put("name", summary.getName());
    o.put("linesOfCode", summary.getLinesOfCode().getLines());
    o.put("issues", summary.getDistribution().getTotal());
    o.put("major", summary.getDistribution().getFirst().getCount());
    o.put("minor", summary.getDistribution().getSecond().getCount());
    o.put("info", summary.getDistribution().getThird().getCount());
    o.put("density", summary.getEvaluation().getRatio());
    o.put("qualified", summary.getEvaluation().isQualified());
    return o;
  }

  /**
   * Adds the data summaries.
   *
   * @param summaries to add
   */
  public void addSummaries(String recipe, List<DataSummary> summaries) throws IOException {
    JSONArray array = new JSONArray();
    summaries.forEach(o -> array.add(toJsonObject(o)));
    put(array, Scope.RECIPE.name(), getMetric().name(), Data.SUMMARIES.name(), recipe);
  }

  /**
   * Adds the data objects.
   *
   * @param recipe to add
   */
  public void addObjects(Recipe recipe) throws IOException, InterruptedException {
    Map<String, List<ViolationData>> group = recipe.objects(clazz)
        .collect(Collectors.groupingBy(ViolationData::getFile));
    for (Entry<String, List<ViolationData>> entry : group.entrySet()) {
      put(JSONArray.fromObject(entry.getValue()),
          Scope.RECIPE.name(), getMetric().name(), Data.OBJECTS.name(),
          recipe.getName(), entry.getKey());
      writeFile(recipe.getName(), entry.getKey());
    }
  }
}
