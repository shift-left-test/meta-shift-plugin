/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import com.lge.plugins.metashift.utils.NamingUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import java.util.Arrays;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildActionTest {

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
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);
  }

  @SafeVarargs
  private final <T> JSONArray newJsonArray(T... values) {
    JSONArray objects = new JSONArray();
    objects.addAll(Arrays.asList(values));
    return objects;
  }

  private void assertValues(JSONObject object, boolean qualified, boolean available,
      double threshold, long denominator, long numerator, double ratio) {
    assertEquals(qualified, object.getBoolean("qualified"));
    assertEquals(available, object.getBoolean("available"));
    assertEquals(threshold, object.getDouble("threshold"), 0.01);
    assertEquals(denominator, object.getInt("denominator"));
    assertEquals(numerator, object.getInt("numerator"));
    assertEquals(ratio, object.getDouble("ratio"), 0.01);
  }

  private void assertCodeSizeDelta(BuildAction action, int recipes, int functions,
      int classes, int files, int lines) {
    JSONObject object = action.getCodeSizeDeltaJson();
    assertEquals(recipes, object.getInt("recipes"));
    assertEquals(functions, object.getInt("functions"));
    assertEquals(classes, object.getInt("classes"));
    assertEquals(files, object.getInt("files"));
    assertEquals(lines, object.getInt("lines"));
  }

  private void assertMetricDelta(BuildAction action, double premirrorCache,
      double sharedStateCache, double codeViolations, double comments, double complexity,
      double statementCoverage, double branchCoverage, double duplications, double mutationTest,
      double recipeViolations, double test) {
    assertEquals(premirrorCache, action.getPremirrorCacheDelta(), 0.01);
    assertEquals(sharedStateCache, action.getSharedStateCacheDelta(), 0.01);
    assertEquals(codeViolations, action.getCodeViolationsDelta(), 0.01);
    assertEquals(comments, action.getCommentsDelta(), 0.01);
    assertEquals(complexity, action.getComplexityDelta(), 0.01);
    assertEquals(statementCoverage, action.getStatementCoverageDelta(), 0.01);
    assertEquals(branchCoverage, action.getBranchCoverageDelta(), 0.01);
    assertEquals(duplications, action.getDuplicationsDelta(), 0.01);
    assertEquals(mutationTest, action.getMutationTestDelta(), 0.01);
    assertEquals(recipeViolations, action.getRecipeViolationsDelta(), 0.01);
    assertEquals(test, action.getTestDelta(), 0.01);
  }

  @Test
  public void testCreate() throws Exception {
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

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    assertEquals("meta-shift report", buildAction.getDisplayName());
    assertEquals("meta-shift-report", buildAction.getUrlName());
    assertEquals(1, buildAction.getRecipes().size());
    assertNotNull(buildAction.getReport());

    JSONObject expected = new JSONObject();
    expected.put("data", buildAction.getReport().getTreemap());
    expected.put("tooltipInfo", buildAction.getReport().getSummaries());

    assertEquals(expected, buildAction.getRecipesTreemapModel());

    JSONArray recipeTableModel = buildAction.getRecipesTableModel();

    assertArrayEquals(new String[]{NamingUtils.getRecipe(fakeRecipe.getName()),},
        recipeTableModel.stream().map(o -> ((JSONObject) o).getString("name")).toArray());

    assertEquals(1, buildAction.getTestedRecipes());

    JSONObject codeSizeJson = buildAction.getCodeSizeJson();
    System.out.println(codeSizeJson);
    assertEquals(1, codeSizeJson.getInt("recipes"));
    assertEquals(0, codeSizeJson.getInt("functions"));
    assertEquals(0, codeSizeJson.getInt("classes"));
    assertEquals(1, codeSizeJson.getInt("files"));
    assertEquals(10, codeSizeJson.getInt("lines"));

    ProjectReport projectReport = buildAction.getReport();
    assertValues(projectReport.getPremirrorCache().getEvaluation(), false, true, 0.8, 0, 0, 0);
    assertValues(projectReport.getSharedStateCache().getEvaluation(), false, true, 0.8, 0, 0, 0);
    assertValues(projectReport.getCodeViolations().getEvaluation(), false, true, 0.1, 10, 6, 0.6);
    assertValues(projectReport.getComments().getEvaluation(), true, true, 0.2, 10, 5, 0.5);
    assertValues(projectReport.getComplexity().getEvaluation(), false, true, 0.1, 11, 5, 0.45);
    assertValues(projectReport.getStatementCoverage().getEvaluation(), false, true, 0.8, 5, 2, 0.4);
    assertValues(projectReport.getBranchCoverage().getEvaluation(), true, true, 0.4, 7, 3, 0.42);
    assertValues(projectReport.getDuplications().getEvaluation(), true, true, 0.1, 10, 0, 0.0);
    assertValues(projectReport.getMutationTests().getEvaluation(), false, true, 0.85, 6, 1, 0.16);
    assertValues(projectReport.getRecipeViolations().getEvaluation(), false, true, 0.1, 10, 6, 0.6);
    assertValues(projectReport.getUnitTests().getEvaluation(), false, true, 0.95, 10, 1, 0.1);
  }

  @Test
  public void testGetPreviousBuildAction() throws Exception {
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

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    assertNull(buildAction.getPreviousBuildAction());
    assertEquals(1.0, buildAction.getTestedRecipesDelta(), 0);
    assertCodeSizeDelta(buildAction, 1, 0, 0, 1, 10);
    assertMetricDelta(buildAction, 0.0, 0.0, 0.6, 0.5, 0.45, 0.4, 0.42, 0.0, 0.16, 0.6, 0.1);

    // second build and check diff.
    FreeStyleBuild run2 = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction2 = run2.getAction(BuildAction.class);

    assertNotNull(buildAction2.getPreviousBuildAction());
    assertEquals(0.0, buildAction2.getTestedRecipesDelta(), 0);
    assertCodeSizeDelta(buildAction2, 0, 0, 0, 0, 0);
    assertMetricDelta(buildAction2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }
}
