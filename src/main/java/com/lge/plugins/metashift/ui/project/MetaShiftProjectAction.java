/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.ui.build.BuildAction;
import edu.hm.hafner.echarts.ChartModelConfiguration;
import edu.hm.hafner.echarts.JacksonFacade;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.echarts.AsyncConfigurableTrendChart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
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

  /**
   * redirect to build action page.
   */
  public void doIndex(StaplerRequest2 req, StaplerResponse2 rsp) throws IOException {
    Integer buildNumber = getLastResultBuildNumber();
    if (buildNumber == null) {
      rsp.sendRedirect2("nodata");
    } else {
      rsp.sendRedirect2("../" + buildNumber + "/meta-shift-report");
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
