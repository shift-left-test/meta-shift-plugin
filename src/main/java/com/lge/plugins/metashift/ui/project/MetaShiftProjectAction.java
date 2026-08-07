/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.ui.MetricView;
import com.lge.plugins.metashift.ui.build.BuildAction;
import edu.hm.hafner.echarts.ChartModelConfiguration;
import edu.hm.hafner.echarts.JacksonFacade;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.echarts.AsyncConfigurableTrendChart;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * The project action class.
 */
public class MetaShiftProjectAction implements ProminentProjectAction,
    AsyncConfigurableTrendChart {

  static final int MAX_TREND_CHART_SERIES = 20;

  private final Run<?, ?> project;

  public MetaShiftProjectAction(Run<?, ?> project) {
    this.project = project;
  }

  public Run<?, ?> getProject() {
    return project;
  }

  @Override
  public String getIconFileName() {
    return "/plugin/meta-shift/img/meta_shift_first.png";
  }

  @Override
  public String getUrlName() {
    return "meta-shift-report";
  }

  @Override
  public String getDisplayName() {
    return "meta-shift report";
  }

  /**
   * Return last successful build action.
   */
  public BuildAction getLastResultBuild() {
    for (Run<?, ?> b = project.getParent().getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }
      BuildAction r = b.getAction(BuildAction.class);
      if (r != null) {
        return r;
      }
    }
    return null;
  }

  /**
   * Return the last successful build number.
   */
  public Integer getLastResultBuildNumber() {
    for (Run<?, ?> b = project.getParent().getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }
      BuildAction r = b.getAction(BuildAction.class);
      if (r != null) {
        return b.getNumber();
      }
    }
    return null;
  }

  private static final int MAX_RECENT_BUILDS = 20;

  /**
   * Returns the most recent builds with a report, newest first, for the recent-builds table on
   * the trend page. Uses the same not-failed/has-report walk as
   * {@link #getConfigurableBuildTrendModel}, but keeps build order (newest first) since this
   * feeds a table rather than a chart.
   *
   * @return recent build rows, newest first
   */
  public List<BuildRow> getRecentBuilds() {
    List<BuildRow> rows = new ArrayList<>();
    for (Run<?, ?> b = project.getParent().getLastSuccessfulBuild();
        b != null && rows.size() < MAX_RECENT_BUILDS; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }
      BuildAction action = b.getAction(BuildAction.class);
      if (action == null || action.getReport() == null) {
        continue;
      }
      rows.add(new BuildRow(b.getNumber(), b.getDisplayName(),
          MetricView.cardsFor(action.getReport(), null, null, null, null)));
    }
    return rows;
  }

  /**
   * One row of the recent-builds table: a build and its four metric cards (deltas not computed,
   * since the table already shows one row per build).
   */
  public static final class BuildRow {

    private final int number;
    private final String displayName;
    private final List<MetricView> cards;

    BuildRow(int number, String displayName, List<MetricView> cards) {
      this.number = number;
      this.displayName = displayName;
      this.cards = cards;
    }

    public int getNumber() {
      return number;
    }

    public String getDisplayName() {
      return displayName;
    }

    public List<MetricView> getCards() {
      return cards;
    }
  }

  /**
   * Returns the build trend chart model as JSON, consumed by the echarts-api trend chart renderer.
   *
   * @param configuration trend configuration (number of builds to consider) as JSON
   * @return the {@link edu.hm.hafner.echarts.line.LinesChartModel} serialized to JSON
   */
  @JavaScriptMethod
  @Override
  public String getConfigurableBuildTrendModel(String configuration) {
    ChartModelConfiguration config = ChartModelConfiguration.fromJson(configuration);
    int maxBuilds = config.isBuildCountDefined() ? config.getBuildCount() : MAX_TREND_CHART_SERIES;

    List<String> buildNames = new ArrayList<>();
    List<Integer> buildNumbers = new ArrayList<>();
    List<ProjectReport> reports = new ArrayList<>();

    for (Run<?, ?> b = project.getParent().getLastSuccessfulBuild();
        b != null && buildNames.size() < maxBuilds; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }
      BuildAction msAction = b.getAction(BuildAction.class);
      if (msAction == null || msAction.getReport() == null) {
        continue;
      }
      buildNames.add(0, b.getDisplayName());
      buildNumbers.add(0, b.getNumber());
      reports.add(0, msAction.getReport());
    }

    return new JacksonFacade().toJson(
        TrendChartModel.create(buildNames, buildNumbers, reports));
  }

  @Override
  public boolean isTrendVisible() {
    return true;
  }
}
