/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.ui.RawReportArchive;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.recipe.RecipeAction;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.FormValidation;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.htmlunit.Page;
import org.htmlunit.html.HtmlPage;
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
  public void testArchiveRawReportsDefaultsToOnAndRoundTrips() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    MetaShiftPublisher before = new MetaShiftPublisher(report.getAbsolutePath());
    // A publisher persisted before this option existed must keep archiving.
    assertTrue(before.isArchiveRawReports());
    before.setArchiveRawReports(false);
    project.getPublishersList().add(before);

    jenkins.submit(client.getPage(project, "configure").getFormByName("config"));

    assertFalse(((MetaShiftPublisher) project.getPublishersList().get(0)).isArchiveRawReports());
  }

  @Test
  public void testArchiveKeepsAssetsThatTheReportPagesReference() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));

    fakeRecipe.add(new FakeSource(10, 4, 5, 6).setTests(1, 2, 3, 4)
        .setStatementCoverage(1, 2).setBranchCoverage(3, 4).setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);
    // gcovr writes its stylesheet and images next to the pages that reference them.
    File coverage = new File(report, fakeRecipe.getName() + "/coverage");
    FileUtils.forceMkdir(coverage);
    FileUtils.writeStringToFile(new File(coverage, "gcov.css"), "body{}", StandardCharsets.UTF_8);
    FileUtils.writeStringToFile(new File(coverage, "index.html"),
        "<link href='gcov.css'/>", StandardCharsets.UTF_8);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    File archive = RawReportArchive.archiveDir(run.getRootDir());
    assertTrue(new File(archive, fakeRecipe.getName() + "/coverage/index.html").isFile());
    assertTrue(new File(archive, fakeRecipe.getName() + "/coverage/gcov.css").isFile());
  }

  @Test
  public void testArchiveCanBeTurnedOff() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    publisher.setArchiveRawReports(false);
    project.getPublishersList().add(publisher);

    fakeRecipe.add(new FakeSource(10, 4, 5, 6).setTests(1, 2, 3, 4)
        .setStatementCoverage(1, 2).setBranchCoverage(3, 4).setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    assertFalse(RawReportArchive.archiveDir(run.getRootDir()).exists());
    jenkins.assertLogContains("Raw report archiving is disabled", run);
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
  public void testPerformLogsPerMetricQualityGate() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    Configuration configuration = new Configuration();
    configuration.setTestThreshold(100);
    configuration.setTestAsUnstable(true);
    publisher.setCustomConfiguration(configuration);
    project.getPublishersList().add(publisher);

    // 1 passed / 10 total tests -> 10%, below the 100% threshold; no coverage report at all
    // (FakeCoverageReport only writes coverage.xml when statement/branch coverage is set).
    fakeRecipe.add(new FakeSource(10, 4, 5, 6).setTests(1, 2, 3, 4));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);
    jenkins.assertLogContains(
        "Unit Tests: 10.0% < threshold 100.0% -> UNSTABLE", run);
    jenkins.assertLogContains(
        "Statement Coverage: no report found; excluded from the build result.", run);
    jenkins.assertLogContains(
        "Branch Coverage: no report found; excluded from the build result.", run);
  }

  @Test
  public void testPerformArchivesRawReportsAndServesThemAsZip() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));

    fakeRecipe.add(new FakeSource(10, 4, 5, 6).setTests(1, 2, 3, 4)
        .setStatementCoverage(1, 2).setBranchCoverage(3, 4).setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    File coverageHtml = FileUtils.getFile(report, fakeRecipe.getName(), "coverage", "index.html");
    FileUtils.forceMkdirParent(coverageHtml);
    FileUtils.writeStringToFile(coverageHtml, "<html>coverage report</html>",
        StandardCharsets.UTF_8);
    // gcovr references its stylesheet from that page; an HTML-only archive would drop it.
    FileUtils.writeStringToFile(
        FileUtils.getFile(report, fakeRecipe.getName(), "coverage", "gcov.css"),
        "body{}", StandardCharsets.UTF_8);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction action = run.getAction(BuildAction.class);
    RecipeAction recipeAction = action.getAction(RecipeAction.class);
    assertTrue(action.hasRawReport(recipeAction.getReportDir()));

    Page zip = client.goTo(
        run.getUrl() + "meta-shift-report/download/" + recipeAction.getReportDir(),
        "application/zip");
    Set<String> entries = new HashSet<>();
    try (ZipInputStream in = new ZipInputStream(
        zip.getWebResponse().getContentAsStream())) {
      for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
        entries.add(entry.getName());
      }
    }
    assertTrue(entries.contains("coverage/index.html"));
    assertTrue(entries.contains("coverage/gcov.css"));
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
