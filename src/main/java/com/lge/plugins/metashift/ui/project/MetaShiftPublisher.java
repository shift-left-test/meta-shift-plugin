/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.analysis.BuildStatusResolver;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.parsers.FileParser;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import hudson.AbortException;
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
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * The post build step class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisher extends Recorder implements SimpleBuildStep {

  private final String reportRoot;
  private Configuration customConfiguration;

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher(String reportRoot) {
    this.reportRoot = reportRoot;
  }

  public String getReportRoot() {
    return reportRoot;
  }

  public boolean isUseCustomConfiguration() {
    return this.customConfiguration != null;
  }

  @DataBoundSetter
  public void setCustomConfiguration(Configuration customConfiguration) {
    this.customConfiguration = customConfiguration;
  }

  public Configuration getCustomConfiguration() {
    return this.customConfiguration;
  }

  /**
   * Plugin descriptor class.
   *
   * <p>
   * Symbol "metashift" is annotated for pipeline job simple usage. pipeline script example below.
   * metashift reportRoot:'report_test/report',  customConfiguration: null
   * </p>
   */
  @Symbol("metashift")
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    // global configuration variable
    private Configuration configuration;

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
      return "Publish the meta-shift report";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindJSON(this.getConfiguration(), formData);
      this.getConfiguration().sanitizeValues();

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
     * Returns the configuration.
     *
     * @return configuration
     */
    public Configuration getConfiguration() {
      if (this.configuration == null) {
        this.configuration = new Configuration();
      }

      return this.configuration;
    }
  }

  private String getFormattedTime(long millis) {
    return DurationFormatUtils.formatDuration(millis, "HH:mm:ss.S");
  }

  private Callable<Void> publishReport(Run<?, ?> run, FilePath reportPath, TaskListener listener,
      Configuration configuration, Recipes recipes) {
    return () -> {
      FilePath buildPath = new FilePath(run.getRootDir());
      DataSource dataSource = new DataSource(new FilePath(buildPath, "meta-shift-report"));

      BuildAction buildAction = new BuildAction(
          run, listener, configuration, dataSource, reportPath, recipes);
      run.addAction(buildAction);

      BuildStatusResolver buildStatus = new BuildStatusResolver(configuration);
      buildStatus.parse(recipes);
      Result buildResult = buildStatus.getCombined();

      Result runResult = run.getResult() == null ? Result.SUCCESS : run.getResult();
      if (runResult.isBetterThan(buildResult)) {
        run.setResult(buildResult);
      }

      return null;
    };
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    PrintStream logger = listener.getLogger();

    Result buildResult = run.getResult();
    if (buildResult != null && buildResult.isWorseThan(Result.SUCCESS)) {
      logger.println("[meta-shift-plugin] Skipping the report as build was not SUCCESS.");
      return;
    }

    logger.println("[meta-shift-plugin] Scanning for the meta-shift report...");

    Instant started = Instant.now();
    logger.printf("[meta-shift-plugin] Started at %s%n", started.toString());

    EnvVars env = run.getEnvironment(listener);
    FilePath reportPath = workspace.child(env.expand(this.reportRoot));
    logger.printf("[meta-shift-plugin] Searching for all report files in %s%n", reportPath);

    if (!reportPath.exists()) {
      throw new AbortException("No report directory found in " + reportPath);
    }

    Configuration configuration = this.customConfiguration;
    if (configuration == null) {
      configuration = ((DescriptorImpl) getDescriptor()).getConfiguration();
    }

    try {
      Recipes recipes = new FileParser(listener.getLogger()).parse(reportPath);

      ExecutorServiceUtils.invoke(publishReport(run, reportPath, listener, configuration, recipes));

      Instant finished = Instant.now();
      logger.printf("[meta-shift-plugin] Finished at %s%n", finished.toString());

      logger.printf("[meta-shift-plugin] Total time: %s%n",
          getFormattedTime(Duration.between(started, finished).toMillis()));

      logger.println("[meta-shift-plugin] Done.");
    } catch (IllegalArgumentException | IOException e) {
      e.printStackTrace(logger);
      throw new AbortException(e.toString());
    } catch (InterruptedException ignored) {
      run.setResult(Result.ABORTED);
    }
  }

  @Override
  public Action getProjectAction(AbstractProject<?, ?> project) {
    return new MetaShiftProjectAction(project);
  }
}
