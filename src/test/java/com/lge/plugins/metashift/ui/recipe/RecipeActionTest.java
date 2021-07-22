/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
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
    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);

    List<RecipeAction> recipeActions = buildAction.getActions(RecipeAction.class);

    assertEquals(1, recipeActions.size());

    RecipeAction recipeAction = recipeActions.get(0);

    assertEquals(fakeRecipe.getRecipe(), recipeAction.getDisplayName());

    assertCodeSizeDelta(recipeAction, 0, 0, 1, 10);

    JSONObject codeSizeJson = recipeAction.getCodeSizeJson();
    assertFalse(codeSizeJson.getBoolean("qualified"));
    assertEquals(0, codeSizeJson.getInt("functions"));
    assertEquals(0, codeSizeJson.getInt("classes"));

    assertFalse(codeSizeJson.getBoolean("available"));
    assertEquals(1, codeSizeJson.getInt("files"));
    assertEquals(0, codeSizeJson.getInt("threshold"));
    assertEquals(10, codeSizeJson.getInt("lines"));
    assertEquals(0, codeSizeJson.getInt("denominator"));
    assertEquals(0, codeSizeJson.getInt("numerator"));
    assertEquals(0, codeSizeJson.getInt("ratio"));

    assertValues(recipeAction.getPremirrorCacheJson(), false, true, 0.8, 0, 0, 0);
    assertValues(recipeAction.getSharedStateCacheJson(), false, true, 0.8, 0, 0, 0);
    assertValues(recipeAction.getCodeViolationsJson(), false, true, 0.1, 10, 6, 0.6);
    assertValues(recipeAction.getCommentsJson(), true, true, 0.2, 10, 5, 0.5);
    assertValues(recipeAction.getComplexityJson(), false, true, 0.1, 11, 5, 0.45);
    assertValues(recipeAction.getStatementCoverageJson(), false, true, 0.8, 3, 1, 0.33);
    assertValues(recipeAction.getBranchCoverageJson(), true, true, 0.4, 7, 3, 0.42);
    assertValues(recipeAction.getDuplicationsJson(), true, true, 0.1, 10, 0, 0.0);
    assertValues(recipeAction.getMutationTestJson(), false, true, 0.85, 6, 1, 0.16);
    assertValues(recipeAction.getRecipeViolationsJson(), false, true, 0.1, 10, 6, 0.6);
    assertValues(recipeAction.getTestJson(), false, true, 0.95, 10, 1, 0.1);

    JSONObject branchCoverageStatistics = recipeAction.getBranchCoverageStatisticsJson();
    assertEquals(0.42, branchCoverageStatistics.getDouble("average"), 0.01);
    assertEquals(0.42, branchCoverageStatistics.getDouble("min"), 0.01);
    assertEquals(0.42, branchCoverageStatistics.getDouble("max"), 0.01);
    assertEquals(1, branchCoverageStatistics.getInt("count"));
    assertEquals(0.42, branchCoverageStatistics.getDouble("sum"), 0.01);
    assertEquals(0.42, branchCoverageStatistics.getDouble("scale"), 0.01);
    assertTrue(branchCoverageStatistics.getBoolean("available"));
    assertTrue(branchCoverageStatistics.getBoolean("percent"));

    JSONObject codeViolationsStatistics = recipeAction.getCodeViolationsStatisticsJson();
    assertEquals(0.6, codeViolationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.6, codeViolationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.6, codeViolationsStatistics.getDouble("max"), 0.01);
    assertEquals(1, codeViolationsStatistics.getInt("count"));
    assertEquals(0.6, codeViolationsStatistics.getDouble("sum"), 0.01);
    assertEquals(0.6, codeViolationsStatistics.getDouble("scale"), 0.01);
    assertTrue(codeViolationsStatistics.getBoolean("available"));
    assertFalse(codeViolationsStatistics.getBoolean("percent"));

    JSONObject commentsStatistics = recipeAction.getCommentsStatisticsJson();
    assertEquals(0.5, commentsStatistics.getDouble("average"), 0.01);
    assertEquals(0.5, commentsStatistics.getDouble("min"), 0.01);
    assertEquals(0.5, commentsStatistics.getDouble("max"), 0.01);
    assertEquals(1, commentsStatistics.getInt("count"));
    assertEquals(0.5, commentsStatistics.getDouble("sum"), 0.01);
    assertEquals(0.5, commentsStatistics.getDouble("scale"), 0.01);
    assertTrue(commentsStatistics.getBoolean("available"));
    assertTrue(commentsStatistics.getBoolean("percent"));

    JSONObject complexityStatistics = recipeAction.getComplexityStatisticsJson();
    assertEquals(0.45, complexityStatistics.getDouble("average"), 0.01);
    assertEquals(0.45, complexityStatistics.getDouble("min"), 0.01);
    assertEquals(0.45, complexityStatistics.getDouble("max"), 0.01);
    assertEquals(1, complexityStatistics.getInt("count"));
    assertEquals(0.45, complexityStatistics.getDouble("sum"), 0.01);
    assertEquals(0.45, complexityStatistics.getDouble("scale"), 0.01);
    assertTrue(complexityStatistics.getBoolean("available"));
    assertTrue(complexityStatistics.getBoolean("percent"));

    JSONObject duplicationsStatistics = recipeAction.getDuplicationsStatisticsJson();
    assertEquals(0.0, duplicationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, duplicationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, duplicationsStatistics.getDouble("max"), 0.01);
    assertEquals(1, duplicationsStatistics.getInt("count"));
    assertEquals(0.0, duplicationsStatistics.getDouble("sum"), 0.01);
    assertEquals(0.0, duplicationsStatistics.getDouble("scale"), 0.01);
    assertTrue(duplicationsStatistics.getBoolean("available"));
    assertTrue(duplicationsStatistics.getBoolean("percent"));

    JSONObject mutationTestStatistics = recipeAction.getMutationTestStatisticsJson();
    assertEquals(0.16, mutationTestStatistics.getDouble("average"), 0.01);
    assertEquals(0.16, mutationTestStatistics.getDouble("min"), 0.01);
    assertEquals(0.16, mutationTestStatistics.getDouble("max"), 0.01);
    assertEquals(1, mutationTestStatistics.getInt("count"));
    assertEquals(0.16, mutationTestStatistics.getDouble("sum"), 0.01);
    assertEquals(0.16, mutationTestStatistics.getDouble("scale"), 0.01);
    assertTrue(mutationTestStatistics.getBoolean("available"));
    assertTrue(mutationTestStatistics.getBoolean("percent"));

    JSONObject premirrorCacheStatistics = recipeAction.getPremirrorCacheStatisticsJson();
    assertEquals(0.0, premirrorCacheStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, premirrorCacheStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, premirrorCacheStatistics.getDouble("max"), 0.01);
    assertEquals(1, premirrorCacheStatistics.getInt("count"));
    assertEquals(0.0, premirrorCacheStatistics.getDouble("sum"), 0.01);
    assertEquals(0.0, premirrorCacheStatistics.getDouble("scale"), 0.01);
    assertTrue(premirrorCacheStatistics.getBoolean("available"));
    assertTrue(premirrorCacheStatistics.getBoolean("percent"));

    JSONObject recipeViolationsStatistics = recipeAction.getRecipeViolationsStatisticsJson();
    assertEquals(0.6, recipeViolationsStatistics.getDouble("average"), 0.01);
    assertEquals(0.6, recipeViolationsStatistics.getDouble("min"), 0.01);
    assertEquals(0.6, recipeViolationsStatistics.getDouble("max"), 0.01);
    assertEquals(1, recipeViolationsStatistics.getInt("count"));
    assertEquals(0.6, recipeViolationsStatistics.getDouble("sum"), 0.01);
    assertEquals(0.6, recipeViolationsStatistics.getDouble("scale"), 0.01);
    assertTrue(recipeViolationsStatistics.getBoolean("available"));
    assertFalse(recipeViolationsStatistics.getBoolean("percent"));

    JSONObject sharedStateCacheStatistics = recipeAction.getSharedStateCacheStatisticsJson();
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("max"), 0.01);
    assertEquals(1, sharedStateCacheStatistics.getInt("count"));
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("sum"), 0.01);
    assertEquals(0.0, sharedStateCacheStatistics.getDouble("scale"), 0.01);
    assertTrue(sharedStateCacheStatistics.getBoolean("available"));
    assertTrue(sharedStateCacheStatistics.getBoolean("percent"));

    JSONObject statementCoverageStatistics = recipeAction.getStatementCoverageStatisticsJson();
    assertEquals(0.33, statementCoverageStatistics.getDouble("average"), 0.01);
    assertEquals(0.33, statementCoverageStatistics.getDouble("min"), 0.01);
    assertEquals(0.33, statementCoverageStatistics.getDouble("max"), 0.01);
    assertEquals(1, statementCoverageStatistics.getInt("count"));
    assertEquals(0.33, statementCoverageStatistics.getDouble("sum"), 0.01);
    assertEquals(0.33, statementCoverageStatistics.getDouble("scale"), 0.01);
    assertTrue(statementCoverageStatistics.getBoolean("available"));
    assertTrue(statementCoverageStatistics.getBoolean("percent"));

    JSONObject testStatistics = recipeAction.getTestStatisticsJson();
    assertEquals(0.1, testStatistics.getDouble("average"), 0.01);
    assertEquals(0.1, testStatistics.getDouble("min"), 0.01);
    assertEquals(0.1, testStatistics.getDouble("max"), 0.01);
    assertEquals(1, testStatistics.getInt("count"));
    assertEquals(0.1, testStatistics.getDouble("sum"), 0.01);
    assertEquals(0.1, testStatistics.getDouble("scale"), 0.01);
    assertTrue(testStatistics.getBoolean("available"));
    assertTrue(testStatistics.getBoolean("percent"));
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

    MetaShiftBuildAction buildAction = run2.getAction(MetaShiftBuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertCodeSizeDelta(recipeAction, 0, 0, 0, 0);
  }
}
