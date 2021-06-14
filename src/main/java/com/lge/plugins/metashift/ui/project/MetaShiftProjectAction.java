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

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.ui.models.BuildTrendModel;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import java.io.IOException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * project action class.
 */
public class MetaShiftProjectAction implements ProminentProjectAction {

  static final int MAX_TREND_CHART_AXIS = 10;

  private final AbstractProject<?, ?> project;

  public MetaShiftProjectAction(AbstractProject<?, ?> project) {
    this.project = project;
  }

  public AbstractProject<?, ?> getProject() {
    return project;
  }

  @Override
  public String getIconFileName() {
    Jenkins jenkins = Jenkins.getInstanceOrNull();
    if (jenkins == null) {
      return "";
    }

    PluginWrapper wrapper = jenkins.getPluginManager().getPlugin(MetaShiftPlugin.class);

    if (wrapper == null) {
      return "";
    }

    return "/plugin/" + wrapper.getShortName() + "/img/meta_shift_first.png";
  }

  @Override
  public String getUrlName() {
    return "metashift_dashboard";
  }

  @Override
  public String getDisplayName() {
    return "Meta Shift Report";
  }

  /**
   * return last successful build action.
   */
  public MetaShiftBuildAction getLastResultBuild() {
    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }
      MetaShiftBuildAction r = b.getAction(MetaShiftBuildAction.class);
      if (r != null) {
        return r;
      }
    }
    return null;
  }

  /**
   * redirect to build action page.
   */
  public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
    MetaShiftBuildAction action = getLastResultBuild();
    if (action == null) {
      rsp.sendRedirect2("nodata");
    } else {
      rsp.sendRedirect2("../../../" + action.getUrl());
    }
  }

  /**
   * return trend chart data.
   *
   * @return trend chart model
   */
  @JavaScriptMethod
  public JSONObject getTrendChartModel() {
    BuildTrendModel model = new BuildTrendModel(MAX_TREND_CHART_AXIS);

    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }

      MetaShiftBuildAction msAction = b.getAction(MetaShiftBuildAction.class);
      if (msAction == null) {
        continue;
      }

      if (!model.addData(b.getDisplayName(), msAction.getMetrics())) {
        break;
      }
    }

    return JSONObject.fromObject(model);
  }
}