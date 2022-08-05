/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.lge.plugins.metashift.builders.RecipeReport;
import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

import java.io.File;
import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Unit tests for the RecipeAction class.
 */
public class RecipeActionTest {

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

  private void assertValues(JSONObject object, boolean qualified, boolean available,
      double threshold, long denominator, long numerator, double ratio) {
    assertEquals(qualified, object.getBoolean("qualified"));
    assertEquals(available, object.getBoolean("available"));
    assertEquals(threshold, object.getDouble("threshold"), 0.01);
    assertEquals(denominator, object.getInt("denominator"));
    assertEquals(numerator, object.getInt("numerator"));
    assertEquals(ratio, object.getDouble("ratio"), 0.01);
  }

  private void assertCodeSizeDelta(RecipeAction action, int functions, int classes, int files,
      int lines) {
    JSONObject object = action.getCodeSizeDeltaJson();
    assertEquals(functions, object.getInt("functions"));
    assertEquals(classes, object.getInt("classes"));
    assertEquals(files, object.getInt("files"));
    assertEquals(lines, object.getInt("lines"));
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

    List<RecipeAction> recipeActions = buildAction.getActions(RecipeAction.class);

    assertEquals(1, recipeActions.size());

    RecipeAction recipeAction = recipeActions.get(0);

    assertEquals(fakeRecipe.getName(), recipeAction.getDisplayName());

    assertCodeSizeDelta(recipeAction, 0, 0, 1, 10);

    RecipeReport recipeReport = recipeAction.getReport();
    assertValues(recipeReport.getPremirrorCache().getEvaluation(), false, true, 0.8, 0, 0, 0);
    assertValues(recipeReport.getSharedStateCache().getEvaluation(), false, true, 0.8, 0, 0, 0);
    assertValues(recipeReport.getCodeViolations().getEvaluation(), false, true, 0.1, 10, 6, 0.6);
    assertValues(recipeReport.getComments().getEvaluation(), true, true, 0.2, 10, 5, 0.5);
    assertValues(recipeReport.getComplexity().getEvaluation(), false, true, 0.1, 11, 5, 0.45);
    assertValues(recipeReport.getStatementCoverage().getEvaluation(), false, true, 0.8, 5, 2, 0.4);
    assertValues(recipeReport.getBranchCoverage().getEvaluation(), true, true, 0.4, 7, 3, 0.42);
    assertValues(recipeReport.getDuplications().getEvaluation(), true, true, 0.1, 10, 0, 0.0);
    assertValues(recipeReport.getMutationTests().getEvaluation(), false, true, 0.85, 6, 1, 0.16);
    assertValues(recipeReport.getRecipeViolations().getEvaluation(), false, true, 0.1, 10, 6, 0.6);
    assertValues(recipeReport.getUnitTests().getEvaluation(), false, true, 0.95, 10, 1, 0.1);

    JSONObject branchCoverageStatistics = recipeReport.getBranchCoverage().getStatistics();
    assertEquals(0.42, branchCoverageStatistics.getDouble("average"), 0.01);
    assertEquals(0.42, branchCoverageStatistics.getDouble("min"), 0.01);
    assertEquals(0.42, branchCoverageStatistics.getDouble("max"), 0.01);

    JSONObject codeViolationsStatistics = recipeReport.getCodeViolations().getStatistics();
    assertEquals(0.6, codeViolationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.6, codeViolationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.6, codeViolationsStatistics.getDouble("max"), 0.01);

    JSONObject commentsStatistics = recipeReport.getComments().getStatistics();
    assertEquals(0.5, commentsStatistics.getDouble("average"), 0.01);
    assertEquals(0.5, commentsStatistics.getDouble("min"), 0.01);
    assertEquals(0.5, commentsStatistics.getDouble("max"), 0.01);

    JSONObject complexityStatistics = recipeReport.getComplexity().getStatistics();
    assertEquals(0.45, complexityStatistics.getDouble("average"), 0.01);
    assertEquals(0.45, complexityStatistics.getDouble("min"), 0.01);
    assertEquals(0.45, complexityStatistics.getDouble("max"), 0.01);

    JSONObject duplicationsStatistics = recipeReport.getDuplications().getStatistics();
    assertEquals(0.0, duplicationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, duplicationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, duplicationsStatistics.getDouble("max"), 0.01);

    JSONObject mutationTestStatistics = recipeReport.getMutationTests().getStatistics();
    assertEquals(0.16, mutationTestStatistics.getDouble("average"), 0.01);
    assertEquals(0.16, mutationTestStatistics.getDouble("min"), 0.01);
    assertEquals(0.16, mutationTestStatistics.getDouble("max"), 0.01);

    JSONObject premirrorCacheStatistics = recipeReport.getPremirrorCache().getStatistics();
    assertEquals(0.0, premirrorCacheStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, premirrorCacheStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, premirrorCacheStatistics.getDouble("max"), 0.01);

    JSONObject recipeViolationsStatistics = recipeReport.getRecipeViolations().getStatistics();
    assertEquals(0.6, recipeViolationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.6, recipeViolationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.6, recipeViolationsStatistics.getDouble("max"), 0.01);

    JSONObject sharedStateCacheStatistics = recipeReport.getSharedStateCache().getStatistics();
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("max"), 0.01);

    JSONObject statementCoverageStatistics = recipeReport.getStatementCoverage().getStatistics();
    assertEquals(0.4, statementCoverageStatistics.getDouble("average"), 0.01);
    assertEquals(0.4, statementCoverageStatistics.getDouble("min"), 0.01);
    assertEquals(0.4, statementCoverageStatistics.getDouble("max"), 0.01);

    JSONObject testStatistics = recipeReport.getUnitTests().getStatistics();
    assertEquals(0.1, testStatistics.getDouble("average"), 0.01);
    assertEquals(0.1, testStatistics.getDouble("min"), 0.01);
    assertEquals(0.1, testStatistics.getDouble("max"), 0.01);
  }

  @Test
  public void testDelta() throws Exception {
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
    assertNotNull(run);
    FreeStyleBuild run2 = jenkins.buildAndAssertStatus(Result.SUCCESS, project);

    BuildAction buildAction = run2.getAction(BuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertCodeSizeDelta(recipeAction, 0, 0, 0, 0);
  }
}
