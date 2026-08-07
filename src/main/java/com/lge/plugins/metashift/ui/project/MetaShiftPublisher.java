/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import com.lge.plugins.metashift.analysis.BranchCoverageEvaluator;
import com.lge.plugins.metashift.analysis.BuildStatusResolver;
import com.lge.plugins.metashift.analysis.MutationTestEvaluator;
import com.lge.plugins.metashift.analysis.StatementCoverageEvaluator;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.parsers.FileParser;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.HtmlReportServer;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import hudson.AbortException;
import hudson.EnvVars;
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
import org.kohsuke.stapler.StaplerRequest2;

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
    public boolean configure(StaplerRequest2 req, JSONObject formData) throws FormException {
      req.bindJSON(this.getConfiguration(), formData);
      this.getConfiguration().sanitizeValues();

      save();

      return super.configure(req, formData);
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

        if (threshold < 0 || 100 < threshold) {
          return FormValidation.error("Value must be a value between 0 and 100.");
        }
        return FormValidation.ok();
      } catch (Exception e) {
        return FormValidation.error("Value must be integer number.");
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

  /**
   * Archives the raw meta-shift HTML reports (gcovr coverage, sentinel mutation testing, gtest
   * unit test) with the build, so {@link HtmlReportServer} can serve them later. Best-effort:
   * archiving failures never fail the build, matching the source-storage policy of
   * {@code RecipeReportBuilder.addSources}.
   *
   * @param run        the build to archive the reports with
   * @param reportPath the report root directory, possibly on a remote agent
   * @param logger     for logging
   */
  private void archiveHtmlReports(Run<?, ?> run, FilePath reportPath, PrintStream logger) {
    try {
      FilePath target = new FilePath(HtmlReportServer.archiveDir(run.getRootDir()));
      int count = reportPath.copyRecursiveTo("**/*.html", target);
      logger.printf("[meta-shift-plugin] Archived %d raw HTML report file(s).%n", count);
    } catch (IOException | InterruptedException e) {
      logger.println("[meta-shift-plugin] WARNING: failed to archive the raw HTML reports; "
          + "the raw report links will be unavailable for this build.");
      e.printStackTrace(logger);
    }
  }

  /**
   * Logs the per-metric quality gate judgment, so a build result change (or the lack of one)
   * is traceable without cross-referencing the report pages.
   *
   * @param recipes       to evaluate
   * @param configuration for evaluation and unstable thresholds
   * @param logger        for logging
   */
  private void logQualityGate(Recipes recipes, Configuration configuration, PrintStream logger) {
    logMetric(logger, "Unit Tests", new UnitTestEvaluator(configuration).parse(recipes),
        configuration.isTestAsUnstable());
    logMetric(logger, "Statement Coverage",
        new StatementCoverageEvaluator(configuration).parse(recipes),
        configuration.isStatementCoverageAsUnstable());
    logMetric(logger, "Branch Coverage",
        new BranchCoverageEvaluator(configuration).parse(recipes),
        configuration.isBranchCoverageAsUnstable());
    logMetric(logger, "Mutation Tests",
        new MutationTestEvaluator(configuration).parse(recipes),
        configuration.isMutationTestAsUnstable());
  }

  private void logMetric(PrintStream logger, String name, Evaluation evaluation,
      boolean asUnstable) {
    if (!evaluation.isAvailable()) {
      logger.printf(
          "[meta-shift-plugin] %s: no report found; excluded from the build result.%n", name);
      return;
    }
    double ratio = evaluation.getRatio() * 100;
    double threshold = evaluation.getThreshold() * 100;
    if (evaluation.isQualified()) {
      logger.printf("[meta-shift-plugin] %s: %.1f%% >= threshold %.1f%% -> PASS%n",
          name, ratio, threshold);
    } else if (asUnstable) {
      logger.printf("[meta-shift-plugin] %s: %.1f%% < threshold %.1f%% -> UNSTABLE%n",
          name, ratio, threshold);
    } else {
      logger.printf(
          "[meta-shift-plugin] %s: %.1f%% < threshold %.1f%% -> FAIL (not configured to "
              + "unstable the build)%n", name, ratio, threshold);
    }
  }

  private Callable<Void> publishReport(Run<?, ?> run, TaskListener listener,
      Configuration configuration, FilePath reportPath, Recipes recipes) {
    return () -> {
      FilePath buildPath = new FilePath(run.getRootDir());
      DataSource dataSource = new DataSource(new FilePath(buildPath, "meta-shift-report"));

      BuildAction buildAction = new BuildAction(
          run, listener, configuration, dataSource, reportPath, recipes);
      run.addAction(buildAction);

      logQualityGate(recipes, configuration, listener.getLogger());

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

      ExecutorServiceUtils.invoke(
          publishReport(run, listener, configuration, reportPath, recipes));

      archiveHtmlReports(run, reportPath, logger);

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
}
