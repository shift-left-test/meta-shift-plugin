/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.builders.ProjectGroup;
import com.lge.plugins.metashift.builders.ProjectReport;
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
    private final Function<ProjectReport, ProjectGroup> mapper;
    private final boolean isPercent;

    /**
     * constructor.
     *
     * @param name series name
     * @param type series type. line, bar, etc...
     */
    public BuildTrendSeries(String name, String type, boolean isPercent,
        Function<ProjectReport, ProjectGroup> mapper) {
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
    public void addData(ProjectReport report) {
      JSONObject evaluator = this.mapper.apply(report).getEvaluation();

      if (evaluator != null && evaluator.getBoolean("available")) {
        if (isPercent) {
          this.data.add(0, evaluator.getDouble("ratio") * 100);
        } else {
          this.data.add(0, evaluator.getDouble("ratio"));
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
        "PremirrorCache", "line", true, ProjectReport::getPremirrorCache));
    this.seriesList.add(new BuildTrendSeries(
        "SharedStateCache", "line", true, ProjectReport::getSharedStateCache));
    this.seriesList.add(new BuildTrendSeries(
        "RecipeViolation", "line", false, ProjectReport::getRecipeViolations));
    this.seriesList.add(new BuildTrendSeries(
        "Comment", "line", true, ProjectReport::getComments));
    this.seriesList.add(new BuildTrendSeries(
        "CodeViolation", "line", false, ProjectReport::getCodeViolations));
    this.seriesList.add(new BuildTrendSeries(
        "Complexity", "line", true, ProjectReport::getComplexity));
    this.seriesList.add(new BuildTrendSeries(
        "Duplication", "line", true, ProjectReport::getDuplications));
    this.seriesList.add(new BuildTrendSeries(
        "Test", "line", true, ProjectReport::getUnitTests));
    this.seriesList.add(new BuildTrendSeries(
        "StatementCoverage", "line", true, ProjectReport::getStatementCoverage));
    this.seriesList.add(new BuildTrendSeries(
        "BranchCoverage", "line", true, ProjectReport::getBranchCoverage));
    this.seriesList.add(new BuildTrendSeries(
        "Mutation", "line", true, ProjectReport::getMutationTests));
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
   * @param report    project report
   * @return is exceeded max build count.
   */
  public boolean addData(String buildName, ProjectReport report) {
    if (buildNameList.size() >= this.maxBuildCount) {
      return false;
    }
    if (report != null) {
      buildNameList.add(0, buildName);
      seriesList.forEach(o -> o.addData(report));
    }
    return true;
  }

  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}
