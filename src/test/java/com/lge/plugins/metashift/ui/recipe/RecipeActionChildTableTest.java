/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.ui.tables.SummaryTableModel;
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
 * Integration tests for the native data-tables-api wiring on {@link RecipeActionChild} (recipe file
 * tables), where the name cell links to the file view rather than a recipe page.
 */
public class RecipeActionChildTableTest {

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

  private RecipeAction recipeAction() {
    return run.getAction(BuildAction.class).getActions(RecipeAction.class).get(0);
  }

  private RecipeActionChild child(String url) {
    return recipeAction().getActions(RecipeActionChild.class).stream()
        .filter(o -> url.equals(o.getUrlName())).findFirst().orElseThrow();
  }

  @Test
  public void testFileTableModelAndRows() {
    RecipeActionChild child = child("statement_coverage");

    assertTrue(child.getTableModel("statement_coverage") instanceof SummaryTableModel);
    List<String> headers = child.getTableModel("statement_coverage").getColumns().stream()
        .map(TableColumn::getHeaderLabel).collect(Collectors.toList());
    // recipe file tables use the "File" name header
    assertEquals(List.of("File", "Statements", "Covered", "Uncovered", "Ratio", "Qualified"),
        headers);

    JSONArray rows = JSONArray.fromObject(child.getTableRows("statement_coverage"));
    assertTrue(rows.size() >= 1);
    JSONObject name = rows.getJSONObject(0).getJSONObject("name");
    // name cell links to the file view on the same page
    assertTrue(name.getString("display").contains(".?file="));
  }

  @Test
  public void testFilePageRendersTableAndKeepsFileView() throws Exception {
    String recipeUrl = recipeAction().getUrlName();
    try (JenkinsRule.WebClient wc = jenkins.createWebClient()) {
      wc.getOptions().setThrowExceptionOnScriptError(false);
      HtmlPage page = wc.getPage(run, "meta-shift-report/" + recipeUrl + "/statement_coverage");
      String content = page.getWebResponse().getContentAsString();
      assertTrue(content.contains("id=\"statement_coverage\""));
      assertTrue(content.contains("File"));
      // the annotated source viewer element must remain on the page
      assertTrue(content.contains("statement-coverage-file-view"));
    }
  }
}
