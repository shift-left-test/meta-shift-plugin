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

import com.lge.plugins.metashift.models.CacheData;
import com.lge.plugins.metashift.models.DataSummary;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * CacheDataWriter class.
 *
 * @author Sung Gon Kim
 */
public class CacheDataWriter extends DataWriter {

  /**
   * Default constructor.
   *
   * @param metric     type
   * @param dataSource for persistent objects
   */
  public CacheDataWriter(Metric metric, DataSource dataSource) {
    super(metric, dataSource);
  }

  @Override
  protected JSONObject toJsonObject(DataSummary summary) {
    JSONObject o = new JSONObject();
    o.put("name", summary.getName());
    o.put("linesOfCode", summary.getLinesOfCode().getLines());
    o.put("signatures", summary.getDistribution().getTotal());
    o.put("hits", summary.getDistribution().getFirst().getCount());
    o.put("misses", summary.getDistribution().getSecond().getCount());
    o.put("ratio", summary.getEvaluation().getRatio());
    o.put("qualified", summary.getEvaluation().isQualified());
    return o;
  }

  /**
   * Adds the data summaries.
   *
   * @param summaries to add
   */
  public void addSummaries(String recipe, List<? extends CacheData> summaries) throws IOException {
    JSONArray array = JSONArray.fromObject(summaries);
    put(array, Scope.RECIPE.name(), getMetric().name(), Data.SUMMARIES.name(), recipe);
  }
}
