/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.ui.tables.EvaluationSummaryTableModel;
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
 * Integration tests for the native data-tables-api overview table on {@link BuildAction}.
 */
public class BuildActionTableTest {

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
        .add(new FakeScript(10, 1, 2, 3))
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
    assertEquals(List.of("Recipes", "Unit Tests", "Statement Coverage", "Branch Coverage",
        "Mutation Tests"), headers);

    JSONArray rows = JSONArray.fromObject(action.getTableRows("recipe-list"));
    assertEquals(1, rows.size());
    JSONObject row = rows.getJSONObject(0);
    assertTrue(row.getJSONObject("name").getString("display").contains("<a "));
    assertTrue(row.containsKey("unitTests"));
    assertTrue(row.containsKey("mutationTests"));
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
}
