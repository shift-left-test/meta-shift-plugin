/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.ui.tables.FileSummaryTableModel;
import com.lge.plugins.metashift.ui.tables.TestListTableModel;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.datatables.TableColumn;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.htmlunit.html.HtmlPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration tests for the consolidated recipe page: the four metric tables are
 * served by {@link RecipeAction} itself instead of per-metric child actions.
 */
public class RecipeActionTableTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleBuild run;

  @Before
  public void setUp() throws Exception {
    File workspace = new File(folder.getRoot(), "workspace");
    File report = new File(workspace, "report");
    FakeReportBuilder builder = new FakeReportBuilder();
    FakeRecipe fakeRecipe = new FakeRecipe(new File(workspace, "source"));

    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));

    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
  }

  private RecipeAction recipeAction() {
    return run.getAction(BuildAction.class).getActions(RecipeAction.class).get(0);
  }

  @Test
  public void testFileTableModelAndRows() {
    RecipeAction action = recipeAction();

    assertTrue(action.getTableModel("files") instanceof FileSummaryTableModel);
    List<String> headers = action.getTableModel("files").getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("File", "Statement", "Branch", "Mutation", "Qualified"), headers);

    JSONArray rows = JSONArray.fromObject(action.getTableRows("files"));
    assertTrue(rows.size() >= 1);
    JSONObject name = rows.getJSONObject(0).getJSONObject("name");
    assertTrue(name.getString("display").contains("<a "));
  }

  @Test
  public void testUnknownTableIdIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> recipeAction().getTableModel("statement_coverage"));
  }

  @Test
  public void testUnitTestListTableModel() {
    RecipeAction action = recipeAction();

    assertTrue(action.getTableModel("unit_tests") instanceof TestListTableModel);
    JSONArray rows = JSONArray.fromObject(action.getTableRows("unit_tests"));
    assertTrue(rows.size() >= 1);
    assertTrue(rows.getJSONObject(0).getJSONObject("status").getString("display")
        .contains("badge"));
  }

  @Test
  public void testRecipePageRendersAllSections() throws Exception {
    String recipeUrl = recipeAction().getUrlName();
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/" + recipeUrl);
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("id=\"unit_tests\""));
      assertTrue(content.contains("id=\"files\""));
    }
  }

  @Test
  public void testFileTableLinksToFileDetail() {
    JSONArray rows = JSONArray.fromObject(recipeAction().getTableRows("files"));
    JSONObject name = rows.getJSONObject(0).getJSONObject("name");
    assertTrue(name.getString("display").contains("file?name="));
  }

  @Test
  public void testFileDetailPageRendersAnnotatedSource() throws Exception {
    RecipeAction action = recipeAction();
    // the name cell's sort value carries the raw file name
    String file = JSONArray.fromObject(action.getTableRows("files"))
        .getJSONObject(0).getJSONObject("name").getString("sort");
    String recipeUrl = action.getUrlName();
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/" + recipeUrl
          + "/file?name=" + hudson.Util.rawEncode(file));
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("msp-source"));
      assertTrue(content.contains("id=\"L1\""));
    }
  }

  @Test
  public void testFileDetailPageFallsBackWithoutStoredSource() throws Exception {
    File workspace = new File(folder.getRoot(), "workspace2");
    File report = new File(workspace, "report");
    FakeRecipe fakeRecipe = new FakeRecipe(new File(workspace, "source"));
    fakeRecipe.add(new FakeSource(10, 4, 5, 6)
        .setTests(1, 0, 0, 0)
        .setStatementCoverage(1, 2));
    new FakeReportBuilder().add(fakeRecipe).toFile(report);
    // no metadata.json -> no source dir -> nothing stored
    for (String task : new String[]{"test", "coverage", "checktest"}) {
      org.apache.commons.io.FileUtils.deleteQuietly(
          org.apache.commons.io.FileUtils.getFile(report, fakeRecipe.getName(), task,
              "metadata.json"));
    }

    FreeStyleProject project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));
    FreeStyleBuild build = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    RecipeAction action =
        build.getAction(BuildAction.class).getActions(RecipeAction.class).get(0);
    String file = JSONArray.fromObject(action.getTableRows("files"))
        .getJSONObject(0).getJSONObject("name").getString("sort");
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(build, "meta-shift-report/" + action.getUrlName()
          + "/file?name=" + hudson.Util.rawEncode(file));
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("Source is not stored for this build"));
      assertTrue(content.contains("Uncovered statement lines"));
    }
  }

  @Test
  public void testFileDetailPageWithUnknownFileShowsWarning() throws Exception {
    String recipeUrl = recipeAction().getUrlName();
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/" + recipeUrl
          + "/file?name=no-such-file.c");
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("was not found in this report"));
    }
  }
}
