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

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.models.Recipes;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import java.io.File;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * MetaShift post build step class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisher extends Recorder implements SimpleBuildStep {

  private final String reportRoot;
  private final Criteria localCriteria;

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher(String reportRoot, Criteria localCriteria) {
    this.reportRoot = reportRoot;
    this.localCriteria = localCriteria;
  }

  public String getReportRoot() {
    return reportRoot;
  }

  public boolean isUseLocalCriteria() {
    return this.localCriteria != null;
  }

  public Criteria getLocalCriteria() {
    return this.localCriteria;
  }

  /**
   * Plugin descriptor class.
   * <p>
   * Symbol "metashift" is annotated for pipeline job simple usage. pipeline script example below.
   * metashift reportRoot:'report_test/report',  localCriteria: null
   * </p>
   */
  @Symbol("metashift")
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    // global criteria variable
    private Criteria criteria;

    /**
     * Default constructor.
     */
    public DescriptorImpl() {
      super(MetaShiftPublisher.class);
      load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> clazz) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Publish Meta-Shift Report";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindJSON(this.getCriteria(), formData);
      save();

      return super.configure(req, formData);
    }

    /**
     * check threshold type field value validation.
     *
     * @param value input value
     *
     * @return form validation
     */
    public FormValidation doCheckThreshold(@QueryParameter String value) {
      String thresholdStr = Util.fixEmptyAndTrim(value);
      if (thresholdStr == null) {
        return FormValidation.error("Value cannot be empty");
      }

      try {
        float threshold = Float.parseFloat(thresholdStr);

        if (threshold < 0 || threshold > 1) {
          return FormValidation.error("Value must be a real value between 0 and 1.");
        }
        return FormValidation.ok();
      } catch (Exception e) {
        return FormValidation.error("Value must be a real value between 0 and 1.");
      }
    }

    /**
     * check limit type field value validation.
     *
     * @param value input value
     *
     * @return form validation
     */
    public FormValidation doCheckLimit(@QueryParameter String value) {
      String limitStr = Util.fixEmptyAndTrim(value);
      if (limitStr == null) {
        return FormValidation.error("Value cannot be empty");
      }

      try {
        int limit = Integer.parseInt(limitStr);

        if (limit < 0) {
          return FormValidation.error("Value must be a positive integer.");
        }
        return FormValidation.ok();
      } catch (Exception e) {
        return FormValidation.error("Value must be a positive integer.");
      }
    }

    /**
     * Returns the criteria.
     *
     * @return criteria
     */
    public Criteria getCriteria() {
      if (this.criteria == null) {
        this.criteria = new Criteria();
      }

      return this.criteria;
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    Result result = run.getResult();

    if (result != null && result.isBetterOrEqualTo(Result.UNSTABLE)) {
      FilePath reportPath = workspace.child(this.reportRoot);
      if (reportPath.exists()) {
        // copy report files
        FilePath buildTarget = new FilePath(run.getRootDir());
        FilePath targetPath = new FilePath(buildTarget, "meta-shift-report");
        listener.getLogger().println("copy report: " + reportPath + " -> " + targetPath);
        reportPath.copyRecursiveTo(targetPath);

        // load recipe list.
        Recipes recipes = new Recipes(new File(targetPath.toURI()));

        // load criteria.
        Criteria criteria = (this.localCriteria == null)
            ? ((DescriptorImpl) getDescriptor()).getCriteria() : this.localCriteria;

        // create action.
        MetaShiftBuildAction buildAction =
            new MetaShiftBuildAction(run, targetPath.toURI(), criteria, recipes);
        run.addAction(buildAction);

        // if not qualified, set result to UNSTABLE
        if (!buildAction.getMetrics().isQualified()) {
          run.setResult(Result.UNSTABLE);
        }
      } else {
        // TODO: how to report invalid report path issue?
        listener.getLogger().println("Meta Shift Error: report path is not exist!!!");
      }
    }
  }
}