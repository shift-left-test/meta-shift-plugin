/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildActionTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleProject project;
  private File report;
  private FakeReportBuilder builder;
  private FakeRecipe fakeRecipe;

  @Before
  public void setUp() throws Exception {
    File workspace = new File(folder.getRoot(), "workspace");
    report = new File(workspace, "report");
    builder = new FakeReportBuilder();
    fakeRecipe = new FakeRecipe(new File(workspace, "source"));

    project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);
  }

  private void assertValues(JSONObject object, boolean qualified, boolean available,
      double threshold, long denominator, long numerator, double ratio) {
    assertEquals(qualified, object.getBoolean("qualified"));
    assertEquals(available, object.getBoolean("available"));
    assertEquals(threshold, object.getDouble("threshold"), 0.01);
    assertEquals(denominator, object.getInt("denominator"));
    assertEquals(numerator, object.getInt("numerator"));
    assertEquals(ratio, object.getDouble("ratio"), 0.01);
  }

  private void assertMetricDelta(BuildAction action, double statementCoverage,
      double branchCoverage, double mutationTest, double test) {
    assertEquals(statementCoverage, action.getStatementCoverageDelta(), 0.01);
    assertEquals(branchCoverage, action.getBranchCoverageDelta(), 0.01);
    assertEquals(mutationTest, action.getMutationTestDelta(), 0.01);
    assertEquals(test, action.getTestDelta(), 0.01);
  }

  private void assertMetricDeltaIsNull(BuildAction action) {
    assertNull(action.getStatementCoverageDelta());
    assertNull(action.getBranchCoverageDelta());
    assertNull(action.getMutationTestDelta());
    assertNull(action.getTestDelta());
  }

  @Test
  public void testCreate() throws Exception {
    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    assertEquals("meta-shift report", buildAction.getDisplayName());
    assertEquals("meta-shift-report", buildAction.getUrlName());
    assertEquals(1, buildAction.getRecipes().size());
    assertNotNull(buildAction.getReport());

    ProjectReport projectReport = buildAction.getReport();
    assertValues(projectReport.getStatementCoverage().getEvaluation(), false, true, 0.8, 5, 2, 0.4);
    assertValues(projectReport.getBranchCoverage().getEvaluation(), true, true, 0.4, 7, 3, 0.42);
    assertValues(projectReport.getMutationTests().getEvaluation(), false, true, 0.85, 6, 1, 0.16);
    assertValues(projectReport.getUnitTests().getEvaluation(), false, true, 0.95, 10, 1, 0.1);
  }

  @Test
  public void testGetPreviousBuildAction() throws Exception {
    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    assertNull(buildAction.getPreviousBuildAction());
    assertMetricDeltaIsNull(buildAction);

    // second build and check diff.
    FreeStyleBuild run2 = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction2 = run2.getAction(BuildAction.class);

    assertNotNull(buildAction2.getPreviousBuildAction());
    assertMetricDelta(buildAction2, 0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void testSummaryCounts() throws Exception {
    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild build = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction action = build.getAction(BuildAction.class);

    long available = action.getMetricCards().stream()
        .filter(com.lge.plugins.metashift.ui.MetricView::isAvailable).count();
    assertEquals(available, action.getAvailableCount());
    assertTrue(action.getQualifiedCount() <= action.getAvailableCount());
    assertTrue(action.getFailedCards().stream()
        .noneMatch(com.lge.plugins.metashift.ui.MetricView::isQualified));
    assertEquals(action.getAvailableCount() - action.getQualifiedCount(),
        action.getFailedCards().size());
  }
}
