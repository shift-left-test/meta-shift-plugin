/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.build.BuildAction;

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
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Unit tests for the MetaShiftProjectAction class.
 */
public class MetaShiftProjectActionTest {

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
  }

  private void assertChartSize(JSONObject object, int legend, int series, int builds) {
    assertEquals(legend, object.getJSONArray("legend").size());
    assertEquals(series, object.getJSONArray("series").size());
    assertEquals(builds, object.getJSONArray("builds").size());
  }

  @Test
  public void testDoIndex() throws Exception {
    MetaShiftProjectAction projectAction = new MetaShiftProjectAction(project);

    StaplerRequest req = mock(StaplerRequest.class);
    StaplerResponse rsp = mock(StaplerResponse.class);

    projectAction.doIndex(req, rsp);

    verify(rsp).sendRedirect2("nodata");

    fakeRecipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 4, 5, 6)
            .setComplexity(10, 5, 6)
            .setCodeViolations(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    projectAction.doIndex(req, rsp);

    verify(rsp).sendRedirect2("../../../" + buildAction.getRun().getUrl() + buildAction.getUrlName() + "/");
  }

  @Test
  public void testGetTrendChartModel() throws Exception {
    // run without meta-shift publisher
    jenkins.buildAndAssertSuccess(project);

    // run with invalid report path
    MetaShiftPublisher publisher = new MetaShiftPublisher("reportDummy");
    project.getPublishersList().add(publisher);

    MetaShiftProjectAction projectAction = new MetaShiftProjectAction(project);

    assertEquals(project, projectAction.getProject());

    fakeRecipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 4, 5, 6)
            .setComplexity(10, 5, 6)
            .setCodeViolations(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    assertChartSize(projectAction.getTrendChartModel(), 11, 11, 0);

    jenkins.buildAndAssertStatus(Result.FAILURE, project);
    project.getPublishersList().remove(publisher);

    // run with valid report path
    publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    assertChartSize(projectAction.getTrendChartModel(), 11, 11, 1);
  }
}
