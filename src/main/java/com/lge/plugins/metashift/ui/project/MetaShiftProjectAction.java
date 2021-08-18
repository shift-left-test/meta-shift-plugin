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
    return "meta-shift-dashboard";
  }

  @Override
  public String getDisplayName() {
    return "meta-shift report";
  }

  /**
   * return last successful build action.
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
   * redirect to build action page.
   */
  public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
    BuildAction action = getLastResultBuild();
    if (action == null) {
      rsp.sendRedirect2("nodata");
    } else {
      rsp.sendRedirect2("../../../" + action.getRun().getUrl() + action.getUrlName() + "/");
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
