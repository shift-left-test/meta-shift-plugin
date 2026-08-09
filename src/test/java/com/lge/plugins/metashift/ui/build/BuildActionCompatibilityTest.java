/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.MetricView;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Backward-compatibility tests for build records published by earlier releases of the plugin.
 *
 * <p>The persisted {@code Report.groups} is an {@code EnumMap} keyed by
 * {@code Constants.Metric}, so every metric constant an older release could write is part of the
 * on-disk format. XStream resolves those keys by name and one unknown name aborts the whole
 * {@code ProjectReport}, which is why these tests load a build record instead of merely
 * restarting: a restart keeps the objects already in memory.
 */
public class BuildActionCompatibilityTest {

  /** Metric constants earlier releases wrote and the current release no longer builds. */
  private static final String[] REMOVED_METRICS = {
      "PREMIRROR_CACHE", "SHARED_STATE_CACHE", "RECIPE_VIOLATIONS", "COMMENTS",
      "CODE_VIOLATIONS", "COMPLEXITY", "DUPLICATIONS", "TESTED_RECIPES",
  };

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleProject project;

  @Before
  public void setUp() throws Exception {
    File workspace = new File(folder.getRoot(), "workspace");
    File report = new File(workspace, "report");
    FakeReportBuilder builder = new FakeReportBuilder();
    FakeRecipe fakeRecipe = new FakeRecipe(new File(workspace, "source"));

    project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));

    fakeRecipe.add(new FakeSource(10, 4, 5, 6)
        .setTests(1, 2, 3, 4)
        .setStatementCoverage(1, 2)
        .setBranchCoverage(3, 4)
        .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);
  }

  /**
   * Rewrites the build record so that the project report carries an extra group keyed by a metric
   * an older release wrote and the current one no longer builds, reproducing what an upgraded
   * Jenkins reads from disk.
   */
  private void injectLegacyMetric(FreeStyleBuild run, String metric) throws Exception {
    File buildXml = new File(run.getRootDir(), "build.xml");
    String xml = new String(Files.readAllBytes(buildXml.toPath()), StandardCharsets.UTF_8);

    int reportAt = xml.indexOf("<projectReport>");
    assertTrue("no project report in the build record", reportAt > 0);
    int entryAt = xml.indexOf("<entry>", reportAt);
    int entryEnd = xml.indexOf("</entry>", entryAt) + "</entry>".length();
    // Copy the first group and rekey it, so the legacy entry lands at the position its ordinal
    // used to occupy and every XStream back-reference still points at the first entry.
    String legacy = xml.substring(entryAt, entryEnd).replace(">NONE<", ">" + metric + "<");

    Files.write(buildXml.toPath(),
        (xml.substring(0, entryEnd) + "\n" + legacy + xml.substring(entryEnd))
            .getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Drops the cached {@code Run} so the next lookup deserializes the build record from disk.
   * {@code Jenkins.reload()} alone does not: {@code RunMap} keeps already-loaded runs.
   */
  private BuildAction reloadBuildAction(int number) {
    project._getRuns().purgeCache();
    FreeStyleBuild reloaded = project.getBuildByNumber(number);
    assertNotNull("build record could not be loaded", reloaded);
    return reloaded.getAction(BuildAction.class);
  }

  private void assertReportIsIntact(BuildAction action) {
    assertNotNull("the build action is missing after reload", action);
    assertNotNull("the project report did not deserialize", action.getReport());
    assertNotNull(action.getReport().getUnitTests());
    assertNotNull(action.getReport().getStatementCoverage());
    assertNotNull(action.getReport().getBranchCoverage());
    assertNotNull(action.getReport().getMutationTests());
    assertEquals("the metric cards are missing", 4, action.getMetricCards().size());
    assertEquals("the recipe count is empty", 1, action.getRecipeCount());
    assertEquals(4, action.getAvailableCount());
  }

  @Test
  public void testReportSurvivesAMetricRemovedIn5dd0ef0() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    injectLegacyMetric(run, "PREMIRROR_CACHE");

    assertReportIsIntact(reloadBuildAction(run.getNumber()));
  }

  @Test
  public void testReportSurvivesAMetricRemovedIn62c3312() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    injectLegacyMetric(run, "TESTED_RECIPES");

    assertReportIsIntact(reloadBuildAction(run.getNumber()));
  }

  @Test
  public void testReportSurvivesEveryRemovedMetricAtOnce() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    for (String metric : REMOVED_METRICS) {
      injectLegacyMetric(run, metric);
    }

    assertReportIsIntact(reloadBuildAction(run.getNumber()));
  }

  /**
   * Rewrites the build record so the project report references a group class that no longer
   * exists, which is the other way a release can leave a report only partly deserialized.
   */
  private void injectUnknownGroupClass(FreeStyleBuild run) throws Exception {
    File buildXml = new File(run.getRootDir(), "build.xml");
    String xml = new String(Files.readAllBytes(buildXml.toPath()), StandardCharsets.UTF_8);

    int reportAt = xml.indexOf("<projectReport>");
    assertTrue("no project report in the build record", reportAt > 0);
    String head = xml.substring(0, reportAt);
    String tail = xml.substring(reportAt)
        .replace("com.lge.plugins.metashift.builders.ProjectGroup",
            "com.lge.plugins.metashift.builders.RemovedGroup");

    Files.write(buildXml.toPath(), (head + tail).getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void testAReportThatCannotBeFullyLoadedRendersAsUnavailable() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    injectUnknownGroupClass(run);

    BuildAction action = reloadBuildAction(run.getNumber());
    assertNotNull("the build action is missing after reload", action);
    assertEquals(4, action.getMetricCards().size());
    assertEquals(0, action.getAvailableCount());
    assertEquals(0, action.getQualifiedCount());
    assertEquals(0, action.getRecipeCount());
    for (MetricView card : action.getMetricCards()) {
      assertFalse(card.getName() + " should be unavailable", card.isAvailable());
      assertEquals("N/A", card.getRatioText());
      assertTrue(card.getBands().isEmpty());
      assertEquals("[]", card.getDistributionJson());
    }
  }

  /**
   * Renaming a job moves its build directories with it. The stored report moves too, so the only
   * thing that can go wrong is the build record remembering where the store used to live. The
   * same code path covers moving a job in or out of a folder and relocating JENKINS_HOME.
   */
  @Test
  public void testReportSurvivesAJobRename() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    assertReportIsIntact(run.getAction(BuildAction.class));

    project.renameTo("renamed-" + project.getName());

    assertReportIsIntact(reloadBuildAction(run.getNumber()));
  }

  @Test
  public void testNewBuildRecordsCarryOnlyTheMetricsStillBuilt() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    String xml = new String(
        Files.readAllBytes(new File(run.getRootDir(), "build.xml").toPath()),
        StandardCharsets.UTF_8);
    for (String metric : REMOVED_METRICS) {
      assertFalse("a compatibility-only metric is written into new build records: " + metric,
          xml.contains(metric));
    }
  }
}
