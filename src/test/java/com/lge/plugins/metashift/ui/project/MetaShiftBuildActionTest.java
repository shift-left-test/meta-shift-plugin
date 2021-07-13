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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
  private TemporaryFileUtils utils;

  @Before
  public void setUp() throws Exception {
    utils = new TemporaryFileUtils(folder);

    project = jenkins.createFreeStyleProject();
    File workspace = utils.getPath("workspace");
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher("report",
        new Configuration());
    project.getPublishersList().add(publisher);
  }

  @Test
  public void testCreate() throws Exception {
    File report = utils.getPath("workspace", "report");
    report.delete();
    FakeReportBuilder builder = new FakeReportBuilder();
    FakeRecipe fakeRecipe = new FakeRecipe(utils.getPath("path", "to", "source"));
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
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);
    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    
    assertEquals("meta-shift report", buildAction.getDisplayName());
    assertEquals(1, buildAction.getRecipes().size());
    assertNotNull(buildAction.getMetrics());

    Map [] expectedSeries = {
      new HashMap<String, Object>() {{
        put("path", "");
        put("link", "");
        put("name", "");
        put("value", new int [] {0, 0});
      }},
      new HashMap<String, Object>() {{
        put("path", "");
        put("link", "");
        put("name", "");
        put("value", new int [] {0, 100});
      }},
      new HashMap<String, Object>() {{
        put("path", "");
        put("link", fakeRecipe.getRecipe());
        put("name", fakeRecipe.getRecipe());
        put("value", new int [] {10, 18});
      }},
    };
    assertEquals(JSONArray.fromObject(expectedSeries),
        buildAction.getRecipesTreemapModel().getJSONArray("series"));

    JSONArray recipeTableModel = buildAction.getRecipesTableModel();
    assertArrayEquals(new String[]{
        fakeRecipe.getRecipe(),
    }, recipeTableModel.stream().map(o ->
        ((JSONObject) o).getString("name")).toArray());

    assertEquals(1, buildAction.getTestedRecipes());

    JSONObject codeSizeJson = buildAction.getCodeSizeJson();
    assertEquals(false, codeSizeJson.getBoolean("qualified"));
    assertEquals(1, codeSizeJson.getInt("recipes"));
    assertEquals(0, codeSizeJson.getInt("functions"));
    assertEquals(0, codeSizeJson.getInt("classes"));
    // TODO: available is false, is it right?
    assertEquals(false, codeSizeJson.getBoolean("available"));
    assertEquals(1, codeSizeJson.getInt("files"));
    assertEquals(0, codeSizeJson.getInt("threshold"));
    assertEquals(10, codeSizeJson.getInt("lines"));
    assertEquals(0, codeSizeJson.getInt("denominator"));
    assertEquals(0, codeSizeJson.getInt("numerator"));
    assertEquals(0, codeSizeJson.getInt("ratio"));

    String[] evaluatorJsonFields = new String[]{"qualified", "available", "threshold",
        "denominator", "numerator", "ratio"};

        JSONObject premirrorCacheJson = buildAction.getPremirrorCacheJson();
        assertEquals(false, premirrorCacheJson.getBoolean("qualified"));
        assertEquals(true, premirrorCacheJson.getBoolean("available"));
        assertEquals(0.8, premirrorCacheJson.getDouble("threshold"), 0.0);
        assertEquals(0, premirrorCacheJson.getInt("denominator"));
        assertEquals(0, premirrorCacheJson.getInt("numerator"));
        assertEquals(0, premirrorCacheJson.getDouble("ratio"), 0.0);
    
        JSONObject sharedStateCacheJson = buildAction.getSharedStateCacheJson();
        assertEquals(false, sharedStateCacheJson.getBoolean("qualified"));
        assertEquals(true, sharedStateCacheJson.getBoolean("available"));
        assertEquals(0.8, sharedStateCacheJson.getDouble("threshold"), 0.0);
        assertEquals(0, sharedStateCacheJson.getInt("denominator"));
        assertEquals(0, sharedStateCacheJson.getInt("numerator"));
        assertEquals(0, sharedStateCacheJson.getDouble("ratio"), 0.0);
    
        JSONObject codeViolationJson = buildAction.getCodeViolationsJson();
        assertEquals(false, codeViolationJson.getBoolean("qualified"));
        assertEquals(true, codeViolationJson.getBoolean("available"));
        assertEquals(0.1, codeViolationJson.getDouble("threshold"), 0.0);
        assertEquals(10, codeViolationJson.getInt("denominator"));
        assertEquals(6, codeViolationJson.getInt("numerator"));
        assertEquals(0.6, codeViolationJson.getDouble("ratio"), 0.0);
    
        JSONObject commentsJson = buildAction.getCommentsJson();
        assertEquals(true, commentsJson.getBoolean("qualified"));
        assertEquals(true, commentsJson.getBoolean("available"));
        assertEquals(0.3, commentsJson.getDouble("threshold"), 0.0);
        assertEquals(10, commentsJson.getInt("denominator"));
        assertEquals(5, commentsJson.getInt("numerator"));
        assertEquals(0.5, commentsJson.getDouble("ratio"), 0.0);
    
        JSONObject complexityJson = buildAction.getComplexityJson();
        assertEquals(false, complexityJson.getBoolean("qualified"));
        assertEquals(true, complexityJson.getBoolean("available"));
        assertEquals(0.1, complexityJson.getDouble("threshold"), 0.0);
        assertEquals(11, complexityJson.getInt("denominator"));
        assertEquals(5, complexityJson.getInt("numerator"));
        assertEquals(0.45, complexityJson.getDouble("ratio"), 0.01);
    
        JSONObject statementCoverageJson = buildAction.getStatementCoverageJson();
        assertEquals(false, statementCoverageJson.getBoolean("qualified"));
        assertEquals(true, statementCoverageJson.getBoolean("available"));
        assertEquals(0.8, statementCoverageJson.getDouble("threshold"), 0.0);
        assertEquals(3, statementCoverageJson.getInt("denominator"));
        assertEquals(1, statementCoverageJson.getInt("numerator"));
        assertEquals(0.33, statementCoverageJson.getDouble("ratio"), 0.01);
    
        JSONObject branchCoverageJson = buildAction.getBranchCoverageJson();
        assertEquals(true, branchCoverageJson.getBoolean("qualified"));
        assertEquals(true, branchCoverageJson.getBoolean("available"));
        assertEquals(0.4, branchCoverageJson.getDouble("threshold"), 0.0);
        assertEquals(7, branchCoverageJson.getInt("denominator"));
        assertEquals(3, branchCoverageJson.getInt("numerator"));
        assertEquals(0.42, branchCoverageJson.getDouble("ratio"), 0.01);
    
        JSONObject duplicationsJson = buildAction.getDuplicationsJson();
        assertEquals(false, duplicationsJson.getBoolean("qualified"));
        assertEquals(true, duplicationsJson.getBoolean("available"));
        assertEquals(0.1, duplicationsJson.getDouble("threshold"), 0.0);
        assertEquals(10, duplicationsJson.getInt("denominator"));
        assertEquals(6, duplicationsJson.getInt("numerator"));
        assertEquals(0.6, duplicationsJson.getDouble("ratio"), 0.0);
    
        JSONObject mutationTestJson = buildAction.getMutationTestJson();
        assertEquals(false, mutationTestJson.getBoolean("qualified"));
        assertEquals(true, mutationTestJson.getBoolean("available"));
        assertEquals(0.85, mutationTestJson.getDouble("threshold"), 0.0);
        assertEquals(6, mutationTestJson.getInt("denominator"));
        assertEquals(1, mutationTestJson.getInt("numerator"));
        assertEquals(0.16, mutationTestJson.getDouble("ratio"), 0.01);
    
        JSONObject recipeViolationsJson = buildAction.getRecipeViolationsJson();
        assertEquals(false, recipeViolationsJson.getBoolean("qualified"));
        assertEquals(true, recipeViolationsJson.getBoolean("available"));
        assertEquals(0.1, recipeViolationsJson.getDouble("threshold"), 0.0);
        assertEquals(10, recipeViolationsJson.getInt("denominator"));
        assertEquals(6, recipeViolationsJson.getInt("numerator"));
        assertEquals(0.6, recipeViolationsJson.getDouble("ratio"), 0.0);
    
        JSONObject testJson = buildAction.getTestJson();
        assertEquals(false, testJson.getBoolean("qualified"));
        assertEquals(true, testJson.getBoolean("available"));
        assertEquals(0.95, testJson.getDouble("threshold"), 0.0);
        assertEquals(10, testJson.getInt("denominator"));
        assertEquals(1, testJson.getInt("numerator"));
        assertEquals(0.1, testJson.getDouble("ratio"), 0.0);    
  }

  @Test
  public void testGetPreviousBuildAction() throws Exception {
    File report = utils.getPath("workspace", "report");
    report.delete();
    FakeReportBuilder builder = new FakeReportBuilder();
    FakeRecipe fakeRecipe = new FakeRecipe(utils.getPath("path", "to", "source"));
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
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);
    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    
    assertNull(buildAction.getPreviousBuildAction());

    assertEquals(1.0, buildAction.getTestedRecipesDelta(), 0);

    assertEquals(1, buildAction.getCodeSizeDeltaJson().getInt("recipes"));
    assertEquals(0, buildAction.getCodeSizeDeltaJson().getInt("functions"));
    assertEquals(0, buildAction.getCodeSizeDeltaJson().getInt("classes"));
    assertEquals(1, buildAction.getCodeSizeDeltaJson().getInt("files"));
    assertEquals(10, buildAction.getCodeSizeDeltaJson().getInt("lines"));

    assertEquals(0.0, buildAction.getPremirrorCacheDelta(), 0.01);
    assertEquals(0.0, buildAction.getSharedStateCacheDelta(), 0.01);
    assertEquals(0.6, buildAction.getCodeViolationsDelta(), 0.01);
    assertEquals(0.5, buildAction.getCommentsDelta(), 0.01);
    assertEquals(0.45, buildAction.getComplexityDelta(), 0.01);
    assertEquals(0.33, buildAction.getStatementCoverageDelta(), 0.01);
    assertEquals(0.42, buildAction.getBranchCoverageDelta(), 0.01);
    assertEquals(0.6, buildAction.getDuplicationsDelta(), 0.01);
    assertEquals(0.16, buildAction.getMutationTestDelta(), 0.01);
    assertEquals(0.6, buildAction.getRecipeViolationsDelta(), 0.01);
    assertEquals(0.1, buildAction.getTestDelta(), 0.01);

    // second build and check diff.
    FreeStyleBuild run2 = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);
    MetaShiftBuildAction buildAction2 = run2.getAction(MetaShiftBuildAction.class);

    assertNotNull(buildAction2.getPreviousBuildAction());

    assertEquals(0.0, buildAction2.getTestedRecipesDelta(), 0);

    assertEquals(0, buildAction2.getCodeSizeDeltaJson().getInt("recipes"));
    assertEquals(0, buildAction2.getCodeSizeDeltaJson().getInt("functions"));
    assertEquals(0, buildAction2.getCodeSizeDeltaJson().getInt("classes"));
    assertEquals(0, buildAction2.getCodeSizeDeltaJson().getInt("files"));
    assertEquals(0, buildAction2.getCodeSizeDeltaJson().getInt("lines"));

    assertEquals(0.0, buildAction2.getPremirrorCacheDelta(), 0.01);
    assertEquals(0.0, buildAction2.getSharedStateCacheDelta(), 0.01);
    assertEquals(0.0, buildAction2.getCodeViolationsDelta(), 0.01);
    assertEquals(0.0, buildAction2.getCommentsDelta(), 0.01);
    assertEquals(0.0, buildAction2.getComplexityDelta(), 0.01);
    assertEquals(0.0, buildAction2.getStatementCoverageDelta(), 0.01);
    assertEquals(0.0, buildAction2.getBranchCoverageDelta(), 0.01);
    assertEquals(0.0, buildAction2.getDuplicationsDelta(), 0.01);
    assertEquals(0.0, buildAction2.getMutationTestDelta(), 0.01);
    assertEquals(0.0, buildAction2.getRecipeViolationsDelta(), 0.01);
    assertEquals(0.0, buildAction2.getTestDelta(), 0.01);
  }
}
