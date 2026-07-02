/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.ui.tables.SummaryTableModel;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.htmlunit.html.HtmlPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration tests for the native data-tables-api wiring on {@link BuildActionChild} (statement
 * coverage recipe table).
 */
public class BuildActionChildTableTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleProject project;
  private FreeStyleBuild run;

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

  private BuildActionChild statementCoverageChild() {
    return run.getAction(BuildAction.class).getActions(BuildActionChild.class).stream()
        .filter(o -> "statement_coverage".equals(o.getUrlName()))
        .findFirst().orElseThrow();
  }

  @Test
  public void testTableModelAndRows() {
    BuildActionChild child = statementCoverageChild();

    assertTrue(child.getTableModel("statement_coverage") instanceof SummaryTableModel);
    assertEquals(6, child.getTableModel("statement_coverage").getColumns().size());

    JSONArray rows = JSONArray.fromObject(child.getTableRows("statement_coverage"));
    assertEquals(1, rows.size());
    JSONObject row = rows.getJSONObject(0);
    // name is a DetailedCell {display, sort}
    assertTrue(row.getJSONObject("name").getString("display").contains("<a "));
    assertNotNull(row.getJSONObject("name").getString("sort"));
    // ratio is a DetailedCell whose display is a percent progress bar
    assertTrue(row.getJSONObject("ratio").getString("display").contains("%"));
  }

  @Test
  public void testAllBuildTablesRenderNatively() throws Exception {
    // metric url -> a distinctive bucket header proving getColumns() rendered via <dt:table>
    String[][] cases = {
        {"statement_coverage", "Covered", "Uncovered"},
        {"branch_coverage", "Covered", "Uncovered"},
        {"mutation_tests", "Killed", "Survived"},
        {"unit_tests", "Passed", "Skipped"},
    };
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      for (String[] c : cases) {
        HtmlPage page = wc.getPage(run, "meta-shift-report/" + c[0]);
        String content = page.getWebResponse().getContentAsString();
        assertTrue(c[0] + " table id", content.contains("id=\"" + c[0] + "\""));
        assertTrue(c[0] + " header " + c[1], content.contains(c[1]));
        assertTrue(c[0] + " header " + c[2], content.contains(c[2]));
      }
    }
  }
}
