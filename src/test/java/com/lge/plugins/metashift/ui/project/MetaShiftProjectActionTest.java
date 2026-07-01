/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
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
        .add(new FakeScript(10, 1, 2, 3))
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
    assertEquals(List.of("Test", "StatementCoverage", "BranchCoverage", "Mutation"), names);
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
      // The echarts-api <c:trend-chart> taglib renders a chart container with this class.
      assertTrue(content.contains("echarts-trend"));
    }
  }
}
