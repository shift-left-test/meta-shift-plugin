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

package com.lge.plugins.metashift.ui.models;

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;

/**
 * trend chart model.
 */
public class BuildTrendModel {

  /**
   * trend chart series data class.
   */
  public static class BuildTrendSeries {

    private final String name;
    private final String type;
    private final List<Double> data;
    private final Function<Metrics, Evaluator<?>> mapper;
    private final boolean isPercent;

    /**
     * constructor.
     *
     * @param name series name
     * @param type series type. line, bar, etc...
     */
    public BuildTrendSeries(String name, String type, boolean isPercent,
        Function<Metrics, Evaluator<?>> mapper) {
      this.name = name;
      this.type = type;
      this.isPercent = isPercent;
      this.mapper = mapper;
      this.data = new ArrayList<>();
    }

    public String getName() {
      return this.name;
    }

    public String getType() {
      return this.type;
    }

    public List<Double> getData() {
      return this.data;
    }

    public int getyAxisIndex() {
      return this.isPercent ? 0 : 1;
    }

    /**
     * add data to series with  evaluator.
     */
    public void addData(Metrics metrics) {
      Evaluator<?> evaluator = this.mapper.apply(metrics);

      if (evaluator != null && evaluator.isAvailable()) {
        if (isPercent) {
          this.data.add(0, evaluator.getRatio() * 100);
        } else {
          this.data.add(0, evaluator.getRatio());
        }
      } else {
        this.data.add(0, null);
      }
    }
  }

  private final int maxBuildCount;
  private final List<String> buildNameList;
  private final List<BuildTrendSeries> seriesList;

  /**
   * constructor.
   *
   * @param maxBuildCount max series size.
   */
  public BuildTrendModel(int maxBuildCount) {
    this.maxBuildCount = maxBuildCount;

    this.buildNameList = new ArrayList<>();
    this.seriesList = new ArrayList<>();

    this.seriesList.add(new BuildTrendSeries(
        "PremirrorCache", "line", true, Metrics::getPremirrorCache));
    this.seriesList.add(new BuildTrendSeries(
        "SharedStateCache", "line", true, Metrics::getSharedStateCache));
    this.seriesList.add(new BuildTrendSeries(
        "RecipeViolation", "line", false, Metrics::getRecipeViolations));
    this.seriesList.add(new BuildTrendSeries(
        "Comment", "line", true, Metrics::getComments));
    this.seriesList.add(new BuildTrendSeries(
        "CodeViolation", "line", false, Metrics::getCodeViolations));
    this.seriesList.add(new BuildTrendSeries(
        "Complexity", "line", true, Metrics::getComplexity));
    this.seriesList.add(new BuildTrendSeries(
        "Duplication", "line", true, Metrics::getDuplications));
    this.seriesList.add(new BuildTrendSeries(
        "Test", "line", true, Metrics::getTest));
    this.seriesList.add(new BuildTrendSeries(
        "StatementCoverage", "line", true, Metrics::getStatementCoverage));
    this.seriesList.add(new BuildTrendSeries(
        "BranchCoverage", "line", true, Metrics::getBranchCoverage));
    this.seriesList.add(new BuildTrendSeries(
        "Mutation", "line", true, Metrics::getMutationTest));
  }

  public List<String> getLegend() {
    return seriesList.stream().map(BuildTrendSeries::getName).collect(Collectors.toList());
  }

  public List<String> getBuilds() {
    return buildNameList;
  }

  public List<BuildTrendSeries> getSeries() {
    return seriesList;
  }

  /**
   * add data to trend chart.
   *
   * @param buildName build name
   * @param metrics   metrics
   * @return is exceeded max build count.
   */
  public boolean addData(String buildName, Metrics metrics) {
    if (buildNameList.size() >= this.maxBuildCount) {
      return false;
    }
    buildNameList.add(0, buildName);
    seriesList.forEach(o -> o.addData(metrics));
    return true;
  }

  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}
