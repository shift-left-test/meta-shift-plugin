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

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
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

public class MetaShiftBuildActionTest {

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

  private JSONObject newJsonObject(JSONArray value, String link, String name,
      JSONObject qualifiedMap) {
    JSONObject object = new JSONObject();
    object.put("path", "");
    object.put("link", link);
    object.put("name", name);
    object.put("target", "_self");
    object.put("value", value);
    object.put("qualifiedMap", qualifiedMap);
    return object;
  }

  private JSONObject newQualifiedMapJsonObject() {
    JSONObject object = new JSONObject();
    object.put("Recipe Violations", false);
    object.put("Duplications", true);
    object.put("Branch Coverage", true);
    object.put("Premirror Cache", false);
    object.put("Shared State Cache", false);
    object.put("Comments", true);
    object.put("Mutation Tests", false);
    object.put("Unit Tests", false);
    object.put("Statement Coverage", false);
    object.put("Code Violations", false);
    object.put("Complexity", false);
    return object;
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

  private void assertCodeSizeDelta(MetaShiftBuildAction action, int recipes, int functions,
      int classes, int files, int lines) {
    JSONObject object = action.getCodeSizeDeltaJson();
    assertEquals(recipes, object.getInt("recipes"));
    assertEquals(functions, object.getInt("functions"));
    assertEquals(classes, object.getInt("classes"));
    assertEquals(files, object.getInt("files"));
    assertEquals(lines, object.getInt("lines"));
  }

  private void assertMetricDelta(MetaShiftBuildAction action, double premirrorCache,
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
    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);

    assertEquals("meta-shift report", buildAction.getDisplayName());
    assertEquals("meta-shift-report", buildAction.getUrlName());
    assertEquals(1, buildAction.getRecipes().size());
    assertNotNull(buildAction.getMetrics());

    JSONArray expected = new JSONArray();
    expected.add(newJsonObject(newJsonArray(0, 0), "", "", new JSONObject()));
    expected.add(newJsonObject(newJsonArray(0, 100), "", "", new JSONObject()));
    expected.add(newJsonObject(newJsonArray(10, 27), fakeRecipe.getRecipe(), fakeRecipe.getRecipe(),
        newQualifiedMapJsonObject()));
    assertEquals(expected, buildAction.getRecipesTreemapModel().getJSONArray("series"));

    JSONArray recipeTableModel = buildAction.getRecipesTableModel();

    assertArrayEquals(new String[]{
        fakeRecipe.getRecipe(),
    }, recipeTableModel.stream().map(o ->
        ((JSONObject) o).getString("name")).toArray());

    assertEquals(1, buildAction.getTestedRecipes());

    JSONObject codeSizeJson = buildAction.getCodeSizeJson();
    assertFalse(codeSizeJson.getBoolean("qualified"));
    assertEquals(1, codeSizeJson.getInt("recipes"));
    assertEquals(0, codeSizeJson.getInt("functions"));
    assertEquals(0, codeSizeJson.getInt("classes"));

    assertFalse(codeSizeJson.getBoolean("available"));
    assertEquals(1, codeSizeJson.getInt("files"));
    assertEquals(0, codeSizeJson.getInt("threshold"));
    assertEquals(10, codeSizeJson.getInt("lines"));
    assertEquals(0, codeSizeJson.getInt("denominator"));
    assertEquals(0, codeSizeJson.getInt("numerator"));
    assertEquals(0, codeSizeJson.getInt("ratio"));

    assertValues(buildAction.getPremirrorCacheJson(), false, true, 0.8, 0, 0, 0);
    assertValues(buildAction.getSharedStateCacheJson(), false, true, 0.8, 0, 0, 0);
    assertValues(buildAction.getCodeViolationsJson(), false, true, 0.1, 10, 6, 0.6);
    assertValues(buildAction.getCommentsJson(), true, true, 0.2, 10, 5, 0.5);
    assertValues(buildAction.getComplexityJson(), false, true, 0.1, 11, 5, 0.45);
    assertValues(buildAction.getStatementCoverageJson(), false, true, 0.8, 3, 1, 0.33);
    assertValues(buildAction.getBranchCoverageJson(), true, true, 0.4, 7, 3, 0.42);
    assertValues(buildAction.getDuplicationsJson(), true, true, 0.1, 10, 0, 0.0);
    assertValues(buildAction.getMutationTestJson(), false, true, 0.85, 6, 1, 0.16);
    assertValues(buildAction.getRecipeViolationsJson(), false, true, 0.1, 10, 6, 0.6);
    assertValues(buildAction.getTestJson(), false, true, 0.95, 10, 1, 0.1);
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
    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);

    assertNull(buildAction.getPreviousBuildAction());
    assertEquals(1.0, buildAction.getTestedRecipesDelta(), 0);
    assertCodeSizeDelta(buildAction, 1, 0, 0, 1, 10);
    assertMetricDelta(buildAction, 0.0, 0.0, 0.6, 0.5, 0.45, 0.33, 0.42, 0.0, 0.16, 0.6, 0.1);

    // second build and check diff.
    FreeStyleBuild run2 = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    MetaShiftBuildAction buildAction2 = run2.getAction(MetaShiftBuildAction.class);

    assertNotNull(buildAction2.getPreviousBuildAction());
    assertEquals(0.0, buildAction2.getTestedRecipesDelta(), 0);
    assertCodeSizeDelta(buildAction2, 0, 0, 0, 0, 0);
    assertMetricDelta(buildAction2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }
}
