/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.ui.tables.EvaluationSummaryTableModel;
import com.lge.plugins.metashift.ui.tables.MutationListTableModel;
import com.lge.plugins.metashift.ui.tables.TestListTableModel;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.datatables.TableColumn;
import io.jenkins.plugins.datatables.TableModel;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.htmlunit.html.HtmlPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration tests for the native data-tables-api overview table on {@link BuildAction}.
 */
public class BuildActionTableTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleBuild run;
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

  @Test
  public void testOverviewTableModelAndRows() {
    BuildAction action = run.getAction(BuildAction.class);

    assertTrue(action.getTableModel("recipe-list") instanceof EvaluationSummaryTableModel);
    List<String> headers = action.getTableModel("recipe-list").getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    assertEquals(List.of("Recipes", "Version", "Unit Tests", "Statement Coverage",
        "Branch Coverage", "Mutation Tests", "Qualified"), headers);

    JSONArray rows = JSONArray.fromObject(action.getTableRows("recipe-list"));
    assertEquals(1, rows.size());
    JSONObject row = rows.getJSONObject(0);
    assertTrue(row.getJSONObject("name").getString("display").contains("<a "));
    assertTrue(row.containsKey("unitTests"));
    assertTrue(row.containsKey("mutationTests"));
  }

  @Test
  public void testProjectWideTestsAndMutationsTableModels() {
    BuildAction action = run.getAction(BuildAction.class);

    TableModel testsModel = action.getTableModel("tests");
    assertTrue(testsModel instanceof TestListTableModel);
    assertEquals(5, testsModel.getRows().size());
    assertEquals(5, action.getFailedTestCount());
    assertFalse(action.isFailedTestsTruncated());

    TableModel mutationsModel = action.getTableModel("mutations");
    assertTrue(mutationsModel instanceof MutationListTableModel);
    assertEquals(2, mutationsModel.getRows().size());
    assertEquals(2, action.getSurvivedMutationCount());
    assertFalse(action.isSurvivedMutationsTruncated());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownTableIdThrows() {
    run.getAction(BuildAction.class).getTableModel("unknown");
  }

  /**
   * Drops every stored object whose key starts with one of the given prefixes, then reloads the
   * build record so the store is read back from disk. Used to prove which stored entries a page
   * actually reads.
   */
  private BuildAction reloadWithoutStoredKeys(String... prefixes) throws Exception {
    File index = new File(new File(run.getRootDir(), "meta-shift-report"), "index.json");
    JSONObject indices = JSONObject.fromObject(
        FileUtils.readFileToString(index, StandardCharsets.UTF_8));
    JSONObject kept = new JSONObject();
    for (Object each : indices.keySet()) {
      String key = (String) each;
      if (Stream.of(prefixes).noneMatch(key::startsWith)) {
        kept.put(key, indices.get(key));
      }
    }
    assertTrue("nothing was dropped, so this proves nothing",
        kept.size() < indices.size());
    FileUtils.writeStringToFile(index, kept.toString(2), StandardCharsets.UTF_8);

    project._getRuns().purgeCache();
    return project.getBuildByNumber(run.getNumber()).getAction(BuildAction.class);
  }

  @Test
  public void testProjectWideTabsDoNotReadEveryRecipesStoredData() throws Exception {
    // The build page used to walk every recipe and deserialize its full test and mutation lists
    // on the request thread. Those per-recipe entries belong to the recipe pages; the build page
    // must serve its tabs from what publishing precomputed.
    BuildAction action = reloadWithoutStoredKeys(
        "RECIPE:UNIT_TESTS:SUMMARIES", "RECIPE:MUTATION_TESTS:FLAT_OBJECTS");

    assertEquals(5, action.getFailedTestCount());
    assertFalse(action.isFailedTestsTruncated());
    assertEquals(5, action.getTableModel("tests").getRows().size());
    assertEquals(2, action.getSurvivedMutationCount());
    assertFalse(action.isSurvivedMutationsTruncated());
    assertEquals(2, action.getTableModel("mutations").getRows().size());
  }

  @Test
  public void testProjectWideTabsStillWorkForBuildsPublishedBeforeTheyWerePrecomputed()
      throws Exception {
    // Builds published by an earlier release carry only the per-recipe entries.
    BuildAction action = reloadWithoutStoredKeys("PROJECT:UNIT_TESTS:FAILED_OBJECTS",
        "PROJECT:MUTATION_TESTS:SURVIVED_OBJECTS", "PROJECT:NONE:COUNTS");

    assertEquals(5, action.getFailedTestCount());
    assertEquals(5, action.getTableModel("tests").getRows().size());
    assertEquals(2, action.getSurvivedMutationCount());
    assertEquals(2, action.getTableModel("mutations").getRows().size());
  }

  @Test
  public void testOverviewPageRendersNativeTable() throws Exception {
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/");
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("id=\"recipe-list\""));
      assertTrue(content.contains("Unit Tests"));
      assertTrue(content.contains("Statement Coverage"));
    }
  }

  @Test
  public void testOverviewShowsAttentionStrip() throws Exception {
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/");
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("Recipes needing attention"));
    }
  }

  @Test
  public void testBuildPageSummaryShowsAllMetrics() throws Exception {
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run);
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("qualified"));
      assertTrue(content.contains("Unit Tests"));
      assertTrue(content.contains("Statement Coverage"));
      assertTrue(content.contains("Branch Coverage"));
      assertTrue(content.contains("Mutation Tests"));
    }
  }
}
