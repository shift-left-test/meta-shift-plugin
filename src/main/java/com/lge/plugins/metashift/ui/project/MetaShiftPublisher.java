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

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.persistence.DataSource;
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
 * The post build step class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisher extends Recorder implements SimpleBuildStep {

  private final String reportRoot;
  private final Configuration localCriteria;

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher(String reportRoot, Configuration localCriteria) {
    this.reportRoot = reportRoot;
    this.localCriteria = localCriteria;
  }

  public String getReportRoot() {
    return reportRoot;
  }

  public boolean isUseLocalCriteria() {
    return this.localCriteria != null;
  }

  public Configuration getLocalCriteria() {
    return this.localCriteria;
  }

  /**
   * Plugin descriptor class.
   *
   * <p>
   * Symbol "metashift" is annotated for pipeline job simple usage. pipeline script example below.
   * metashift reportRoot:'report_test/report',  localCriteria: null
   * </p>
   */
  @Symbol("metashift")
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    // global criteria variable
    private Configuration criteria;

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
    public FormValidation doCheckPercentThreshold(@QueryParameter String value) {
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
    public Configuration getCriteria() {
      if (this.criteria == null) {
        this.criteria = new Configuration();
      }

      return this.criteria;
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    listener.getLogger().printf("%s - begin%n", this.getDescriptor().getDisplayName());

    Result result = run.getResult();

    if (result != null && result.isBetterOrEqualTo(Result.UNSTABLE)) {
      EnvVars env = run.getEnvironment(listener);
      FilePath reportPath = workspace.child(env.expand(this.reportRoot));
      if (reportPath.exists()) {
        Configuration criteria = this.localCriteria;
        if (criteria != null) {
          listener.getLogger().println("Use project criteria");
        } else {
          criteria = ((DescriptorImpl) getDescriptor()).getCriteria();
          listener.getLogger().println("Use global criteria");
        }

        FilePath buildPath = new FilePath(run.getRootDir());
        DataSource dataSource = new DataSource(new FilePath(buildPath, "meta-shift-report"));

        listener.getLogger().println("Create project report");
        MetaShiftBuildAction buildAction = new MetaShiftBuildAction(
            run, listener, criteria, reportPath, dataSource);
        run.addAction(buildAction);

        if (!buildAction.getMetrics().isStable(criteria)) {
          run.setResult(Result.UNSTABLE);
        }
      } else {
        throw new IllegalArgumentException(
            String.format("Meta Shift Error: report path[%s] does not exist!!!",
                reportPath.toURI().toString()));
      }
    }
    listener.getLogger().printf("%s - end%n", this.getDescriptor().getDisplayName());
  }

  @Override
  public Action getProjectAction(AbstractProject<?, ?> project) {
    return new MetaShiftProjectAction(project);
  }
}