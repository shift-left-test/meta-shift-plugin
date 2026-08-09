/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
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
 * Integration tests for the {@link MetaShiftProjectAction} class.
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
    project.getPublishersList().add(new MetaShiftPublisher(report.getName()));
  }

  private void configureReport() throws Exception {
    fakeRecipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);
  }

  @Test
  public void testConfigurableBuildTrendModel() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    MetaShiftProjectAction action = project.getAction(MetaShiftProjectAction.class);
    assertNotNull(action);
    assertTrue(action.isTrendVisible());

    String json = action.getConfigurableBuildTrendModel("{}");
    JSONObject model = JSONObject.fromObject(json);
    assertEquals(2, model.getJSONArray("domainAxisLabels").size());

    JSONArray series = model.getJSONArray("series");
    List<String> names = series.stream()
        .map(o -> ((JSONObject) o).getString("name")).collect(Collectors.toList());
    assertEquals(
        List.of("Unit Tests", "Statement Coverage", "Branch Coverage", "Mutation Tests"), names);
  }

  @Test
  public void testProjectPageRendersTrendChart() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      // HtmlUnit does not play well with JavaScript; we only assert the server-rendered markup.
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(project);
      String content = page.getWebResponse().getContentAsString();
      // The job page widget renders the chart container itself and keeps click-through,
      // because there is no build table beside it. The model is fetched after load.
      assertTrue(content.contains("data-ms-trend"));
      assertTrue(content.contains("data-ms-trend-links"));
      assertTrue(content.contains("resize:both"));
      assertTrue(content.contains("msTrendProxy"));
    }
  }

  @Test
  public void testTrendModelCarriesEveryMetricSeries() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    String json = project.getAction(MetaShiftProjectAction.class)
        .getConfigurableBuildTrendModel("{}");
    assertTrue(json.contains("\"Unit Tests\""));
    assertTrue(json.contains("\"Statement Coverage\""));
    assertTrue(json.contains("\"Branch Coverage\""));
    assertTrue(json.contains("\"Mutation Tests\""));
    assertTrue(json.contains("buildNumbers"));
  }

  @Test
  public void testGetRecentBuildsNewestFirst() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    MetaShiftProjectAction action = project.getAction(MetaShiftProjectAction.class);
    List<MetaShiftProjectAction.BuildRow> rows = action.getRecentBuilds();
    assertEquals(2, rows.size());
    assertEquals(2, rows.get(0).getNumber());
    assertEquals(1, rows.get(1).getNumber());
    assertEquals(4, rows.get(0).getCards().size());
  }

  @Test
  public void testProjectActionPageRendersTrendAndRecentBuilds() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(project, "meta-shift-report/");
      String content = page.getWebResponse().getContentAsString();
      // Current state first, then the trend, then the history: the three sections of the page.
      assertTrue(content.contains("Current status"));
      assertTrue(content.contains("Recent builds"));
      assertTrue(content.contains("data-ms-trend"));
      // Click-through is intentionally absent here; the table below links every build instead.
      assertFalse(content.contains("data-ms-trend-links"));
    }
  }

  @Test
  public void testRecentBuildsCarryResultAndTimestamp() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    MetaShiftProjectAction.BuildRow row =
        project.getAction(MetaShiftProjectAction.class).getRecentBuilds().get(0);
    assertEquals("SUCCESS", row.getResult());
    assertEquals("success", row.getResultClass());
    assertFalse(row.getTimestamp().isEmpty());
  }

  @Test
  public void testLatestCardsExposeEveryMetric() throws Exception {
    configureReport();
    jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    assertEquals(4, project.getAction(MetaShiftProjectAction.class).getLatestCards().size());
  }

}
