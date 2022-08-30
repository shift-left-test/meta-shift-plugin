/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.ui.build.BuildAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * The project action class.
 */
public class MetaShiftProjectAction implements ProminentProjectAction {

  static final int MAX_TREND_CHART_SERIES = 20;

  private final AbstractProject<?, ?> project;

  public MetaShiftProjectAction(AbstractProject<?, ?> project) {
    this.project = project;
  }

  public AbstractProject<?, ?> getProject() {
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
    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
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
    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
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

    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
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
}
