/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.ui.build.BuildAction;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.echarts.AsyncConfigurableTrendChart;
import io.jenkins.plugins.echarts.AsyncTrendChart;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * The project action class.
 */
public class MetaShiftProjectAction implements ProminentProjectAction, AsyncTrendChart,
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
  public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
    Integer buildNumber = getLastResultBuildNumber();
    if (buildNumber == null) {
      rsp.sendRedirect2("nodata");
    } else {
      rsp.sendRedirect2("../" + buildNumber + "/meta-shift-report");
    }
  }

  /**
   * return trend chart data.
   *
   * @return trend chart model
   */
  @JavaScriptMethod
  public JSONObject getTrendChartModel() {
    BuildTrendModel model = new BuildTrendModel(MAX_TREND_CHART_SERIES);

    for (Run<?, ?> b = project.getParent().getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }

      BuildAction msAction = b.getAction(BuildAction.class);
      if (msAction == null) {
        continue;
      }

      if (!model.addData(b.getDisplayName(), msAction.getReport())) {
        break;
      }
    }

    return model.toJsonObject();
  }

  @Override
  public String getConfigurableBuildTrendModel(String configuration) {
    return getTrendChartModel().toString();
  }

  @Override
  public String getBuildTrendModel() {
    return getTrendChartModel().toString();
  }

  @Override
  public boolean isTrendVisible() {
    return true;
  }
}
