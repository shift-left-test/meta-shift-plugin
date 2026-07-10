/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.FormValidation;
import java.io.File;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;

/**
 * Unit tests for the MetaShiftPublisher class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisherTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();
  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  private WebClient client;
  private File workspace;
  private File report;
  private FakeReportBuilder builder;
  private FakeRecipe fakeRecipe;

  @Before
  public void setUp() throws Exception {
    workspace = new File(folder.getRoot(), "workspace");
    report = new File(workspace, "report");
    builder = new FakeReportBuilder();
    fakeRecipe = new FakeRecipe(new File(workspace, "source"));

    // HtmlUnit does not play well with JavaScript
    client = jenkins.createWebClient();
    client.getOptions().setThrowExceptionOnScriptError(false);
  }

  @Test
  public void testConfigureRoundTripWithEmptyDirectory() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    MetaShiftPublisher before = new MetaShiftPublisher(report.getAbsolutePath());
    project.getPublishersList().add(before);

    jenkins.submit(client.getPage(project, "configure").getFormByName("config"));

    jenkins.assertEqualDataBoundBeans(
        new MetaShiftPublisher(report.getAbsolutePath()),
        project.getPublishersList().get(0));
  }

  @Test
  public void testPerform() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);

    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    // verify MetaShiftBuildAction created.
    assertEquals(1, run.getActions(BuildAction.class).size());

    BuildAction action = run.getAction(BuildAction.class);
    assertEquals(run, action.getRun());

    // verify RecipeAction created.
    assertEquals(1, action.getActions(RecipeAction.class).size());
    RecipeAction recipeAction = action.getAction(RecipeAction.class);
    assertEquals(run, recipeAction.getRun());
  }

  @Test
  public void testPerformWithCustomConfiguration() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    publisher.setCustomConfiguration(new Configuration());
    project.getPublishersList().add(publisher);

    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    // verify MetaShiftBuildAction created.
    assertEquals(1, run.getActions(BuildAction.class).size());

    BuildAction action = run.getAction(BuildAction.class);
    assertEquals(run, action.getRun());

    // verify RecipeAction created.
    assertEquals(1, action.getActions(RecipeAction.class).size());
    RecipeAction recipeAction = action.getAction(RecipeAction.class);
    assertEquals(run, recipeAction.getRun());
  }

  @Test
  public void testPipelineJobTest() throws Exception {
    WorkflowJob project = jenkins.createProject(WorkflowJob.class);

    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    project.setDefinition(new CpsFlowDefinition("" +
        "node {" +
        String.format(" metashift reportRoot:'%s'", report.getAbsolutePath()) +
        "}", true));
    WorkflowRun run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    // verify MetaShiftBuildAction created.
    assertEquals(1, run.getActions(BuildAction.class).size());

    BuildAction action = run.getAction(BuildAction.class);
    assertEquals(run, action.getRun());

    // verify RecipeAction created.
    assertEquals(1, action.getActions(RecipeAction.class).size());
    RecipeAction recipeAction = action.getAction(RecipeAction.class);
    assertEquals(run, recipeAction.getRun());
  }

  @Test
  public void testPerformReportPathException() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher("reportDummy");
    project.getPublishersList().add(publisher);

    // Fail with invalid report path.
    jenkins.buildAndAssertStatus(Result.FAILURE, project);
  }

  @Test
  public void testDoCheckPercentThreshold() {
    MetaShiftPublisher.DescriptorImpl impl = new MetaShiftPublisher.DescriptorImpl();

    assertEquals(FormValidation.ok(), impl.doCheckPercentThreshold("0"));
    assertEquals(FormValidation.ok(), impl.doCheckPercentThreshold("10"));
    assertEquals(FormValidation.ok(), impl.doCheckPercentThreshold("100"));

    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("0.0"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("0.0001"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("10.0"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("10.1"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("110"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold(null));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("-10"));
    assertNotEquals(FormValidation.ok(), impl.doCheckPercentThreshold("test"));
  }

}
