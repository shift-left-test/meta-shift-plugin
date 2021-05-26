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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.metrics.Evaluator;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * project action class.
 */
public class MetaShiftProjectAction implements ProminentProjectAction {

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
   * trend chart series data class.
   */
  public static class TrendChartSeries {

    private final String name;
    private final String type;
    private final List<Double> data;

    public String getName() {
      return this.name;
    }

    public String getType() {
      return this.type;
    }

    public List<Double> getData() {
      return this.data;
    }

    /**
     * add data to series with  evaluator.
     *
     * @param evaluator evaluator
     */
    public void addData(Evaluator<?> evaluator) {
      if (evaluator != null && evaluator.isAvailable()) {
        this.data.add(0, evaluator.getRatio() * 100);
      } else {
        this.data.add(0, null);
      }
    }

    /**
     * constructor.
     *
     * @param name series name
     * @param type series type. line, bar, etc...
     */
    public TrendChartSeries(String name, String type) {
      this.name = name;
      this.type = type;
      this.data = new ArrayList<>();
    }
  }

  /**
   * return trend chart data.
   *
   * @return trend chart model
   */
  @JavaScriptMethod
  public JSONObject getTrendChartModel() {
    TrendChartSeries seriesPremirrorCache = new TrendChartSeries("PremirrorCache", "line");
    TrendChartSeries seriesSharedStateCache = new TrendChartSeries("SharedStateCache", "line");
    TrendChartSeries seriesRecipeViolation = new TrendChartSeries("RecipeViolation", "line");
    TrendChartSeries seriesComment = new TrendChartSeries("Comment", "line");
    TrendChartSeries seriesCodeViolation = new TrendChartSeries("CodeViolation", "line");
    TrendChartSeries seriesComplexity = new TrendChartSeries("Complexity", "line");
    TrendChartSeries seriesDuplication = new TrendChartSeries("Duplication", "line");
    TrendChartSeries seriesTest = new TrendChartSeries("Test", "line");
    TrendChartSeries seriesCoverage = new TrendChartSeries("Coverage", "line");
    TrendChartSeries seriesMutation = new TrendChartSeries("Mutation", "line");

    List<String> buildNameList = new ArrayList<>();

    for (AbstractBuild<?, ?> b = project.getLastSuccessfulBuild();
        b != null; b = b.getPreviousNotFailedBuild()) {
      if (b.getResult() == Result.FAILURE) {
        continue;
      }

      MetaShiftBuildAction msAction = b.getAction(MetaShiftBuildAction.class);
      if (msAction == null) {
        continue;
      }
      buildNameList.add(0, b.getDisplayName());

      seriesPremirrorCache.addData(msAction.getMetrics().getPremirrorCache());
      seriesSharedStateCache.addData(msAction.getMetrics().getSharedStateCache());
      seriesRecipeViolation.addData(msAction.getMetrics().getRecipeViolations());
      seriesComment.addData(msAction.getMetrics().getComments());
      seriesCodeViolation.addData(msAction.getMetrics().getCodeViolations());
      seriesComplexity.addData(msAction.getMetrics().getComplexity());
      seriesDuplication.addData(msAction.getMetrics().getDuplications());
      seriesTest.addData(msAction.getMetrics().getTest());
      seriesCoverage.addData(msAction.getMetrics().getCoverage());
      seriesMutation.addData(msAction.getMetrics().getMutationTest());

      // TODO: check response series size
      if (buildNameList.size() >= 10) {
        break;
      }
    }

    JSONObject model = new JSONObject();
    model.put("legend", new String[]{
        seriesSharedStateCache.getName(), seriesPremirrorCache.getName(),
        seriesRecipeViolation.getName(), seriesComment.getName(),
        seriesCodeViolation.getName(), seriesComplexity.getName(), seriesDuplication.getName(),
        seriesTest.getName(), seriesCoverage.getName(), seriesMutation.getName()
    });
    model.put("builds", buildNameList);
    model.put("series", new TrendChartSeries[]{
        seriesSharedStateCache, seriesPremirrorCache,
        seriesRecipeViolation, seriesComment, seriesCodeViolation,
        seriesComplexity, seriesDuplication, seriesTest, seriesCoverage, seriesMutation
    });

    return model;
  }
}