/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.builders.ProjectGroup;
import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.ui.tables.TableHtml;
import edu.hm.hafner.echarts.line.LineSeries;
import edu.hm.hafner.echarts.line.LineSeries.FilledMode;
import edu.hm.hafner.echarts.line.LineSeries.StackedMode;
import edu.hm.hafner.echarts.line.LinesChartModel;
import io.jenkins.plugins.echarts.JenkinsPalette;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.sf.json.JSONObject;

/**
 * Builds the echarts-api trend chart model ({@link LinesChartModel}) from a series of project
 * reports. Each metric becomes one line whose values are the qualification ratio in percent;
 * unavailable metrics are rendered as gaps.
 */
final class TrendChartModel {

  /**
   * Definition of a single trend line: its label, color and the metric group it reads.
   */
  private static final class SeriesDef {

    private final String name;
    private final String color;
    private final Function<ProjectReport, ProjectGroup> mapper;

    private SeriesDef(String name, JenkinsPalette color,
        Function<ProjectReport, ProjectGroup> mapper) {
      this.name = name;
      this.color = color.normal();
      this.mapper = mapper;
    }
  }

  private static final List<SeriesDef> SERIES = List.of(
      new SeriesDef("Unit Tests", JenkinsPalette.BLUE, ProjectReport::getUnitTests),
      new SeriesDef("Statement Coverage", JenkinsPalette.GREEN,
          ProjectReport::getStatementCoverage),
      new SeriesDef("Branch Coverage", JenkinsPalette.YELLOW,
          ProjectReport::getBranchCoverage),
      new SeriesDef("Mutation Tests", JenkinsPalette.RED, ProjectReport::getMutationTests));

  private TrendChartModel() {
  }

  /**
   * Creates a line chart model. The three input lists are ordered oldest build first and must share
   * the same length.
   *
   * @param buildNames   x-axis labels (build display names)
   * @param buildNumbers build numbers aligned with {@code buildNames}
   * @param reports      project reports aligned with {@code buildNames}
   * @return the trend chart model
   */
  static LinesChartModel create(List<String> buildNames, List<Integer> buildNumbers,
      List<ProjectReport> reports) {
    LinesChartModel model = new LinesChartModel(buildNames, buildNumbers);
    for (SeriesDef def : SERIES) {
      LineSeries series =
          new LineSeries(def.name, def.color, StackedMode.SEPARATE_LINES, FilledMode.LINES);
      List<Double> data = new ArrayList<>();
      for (ProjectReport report : reports) {
        data.add(percentValue(def.mapper.apply(report)));
      }
      series.addAll(data);
      model.addSeries(series);
    }
    return model;
  }

  /**
   * Returns the qualification ratio of a metric in percent, or {@code null} when the metric is not
   * available so the chart renders a gap instead of a zero.
   */
  private static Double percentValue(ProjectGroup group) {
    JSONObject evaluation = group.getEvaluation();
    if (evaluation != null && evaluation.optBoolean("available", false)) {
      return (double) TableHtml.percent(evaluation.getDouble("ratio"));
    }
    return null;
  }
}
