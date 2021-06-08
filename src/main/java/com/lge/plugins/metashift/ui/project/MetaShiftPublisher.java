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

import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.CriteriaWithOptions;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
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
  private final CriteriaWithOptions localCriteria;
  
  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher(String reportRoot, CriteriaWithOptions localCriteria) {
    this.reportRoot = reportRoot;
    this.localCriteria = localCriteria;
  }

  public String getReportRoot() {
    return reportRoot;
  }

  public boolean isUseLocalCriteria() {
    return this.localCriteria != null;
  }

  public CriteriaWithOptions getLocalCriteria() {
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
    private CriteriaWithOptions criteria;

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
     * check double type threshold value validation.
     *
     * @param value input value
     * @return form validation
     */
    public FormValidation doCheckThreshold(@QueryParameter String value) {
      String thresholdStr = Util.fixEmptyAndTrim(value);
      if (thresholdStr == null) {
        return FormValidation.error("Value cannot be empty");
      }

      try {
        float threshold = Float.parseFloat(thresholdStr);

        if (threshold < 0) {
          return FormValidation.error("Value must be a positive real value.");
        }
        return FormValidation.ok();
      } catch (Exception e) {
        return FormValidation.error("Value must be a real number.");
      }
    }

    /**
     * check percent type threshold value validation.
     */
    public FormValidation doCheckPrecentThreshold(@QueryParameter String value) {
      String thresholdStr = Util.fixEmptyAndTrim(value);
      if (thresholdStr == null) {
        return FormValidation.error("Value cannot be empty");
      }

      try {
        int threshold = Integer.parseInt(thresholdStr);

        if (threshold < 0 || threshold > 100) {
          return FormValidation.error("Value must be a value between 0 and 100.");
        }
        return FormValidation.ok();
      } catch (Exception e) {
        return FormValidation.error("Value must be integer number.");
      }
    }

    /**
     * check int type limit value validation.
     *
     * @param value input value
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
    public CriteriaWithOptions getCriteria() {
      if (this.criteria == null) {
        this.criteria = new CriteriaWithOptions();
      }

      return this.criteria;
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    Result result = run.getResult();

    if (result != null && result.isBetterOrEqualTo(Result.UNSTABLE)) {
      EnvVars env = run.getEnvironment(listener);
      FilePath reportPath = workspace.child(env.expand(this.reportRoot));
      if (reportPath.exists()) {
        // load criteria.
        CriteriaWithOptions criteria = (this.localCriteria == null)
            ? ((DescriptorImpl) getDescriptor()).getCriteria() : this.localCriteria;

        FilePath buildPath = new FilePath(run.getRootDir());
        DataSource dataSource = new DataSource(new FilePath(buildPath, "meta-shift-report"));

        // create action.
        MetaShiftBuildAction buildAction = new MetaShiftBuildAction(
            run, listener, criteria, reportPath, dataSource);
        run.addAction(buildAction);

        boolean isStable = true;

        if (criteria.isMarkUnstablePremirrorCache()) {
          isStable &= buildAction.getMetrics().getPremirrorCache().isQualified();
        }
        if (criteria.isMarkUnstableSharedStateCache()) {
          isStable &= buildAction.getMetrics().getSharedStateCache().isQualified();
        }
        if (criteria.isMarkUnstableRecipeViolation()) {
          isStable &= buildAction.getMetrics().getRecipeViolations().isQualified();
        }
        if (criteria.isMarkUnstableComment()) {
          isStable &= buildAction.getMetrics().getComments().isQualified();
        }
        if (criteria.isMarkUnstableCodeViolation()) {
          isStable &= buildAction.getMetrics().getCodeViolations().isQualified();
        }
        if (criteria.isMarkUnstableComplexity()) {
          isStable &= buildAction.getMetrics().getComplexity().isQualified();
        }
        if (criteria.isMarkUnstableDuplication()) {
          isStable &= buildAction.getMetrics().getDuplications().isQualified();
        }
        if (criteria.isMarkUnstableTest()) {
          isStable &= buildAction.getMetrics().getTest().isQualified();
        }
        if (criteria.isMarkUnstableCoverage()) {
          isStable &= buildAction.getMetrics().getCoverage().isQualified();
        }
        if (criteria.isMarkUnstableMutationTest()) {
          isStable &= buildAction.getMetrics().getMutationTest().isQualified();
        }

        if (!isStable) {
          run.setResult(Result.UNSTABLE);
        }
      } else {
        listener.getLogger().printf("Meta Shift Error: report path[%s] does not exist!!!",
            reportPath.toURI().toString());
      }
    }
  }

  @Override
  public Action getProjectAction(AbstractProject<?, ?> project) {
    return new MetaShiftProjectAction(project);
  }
}