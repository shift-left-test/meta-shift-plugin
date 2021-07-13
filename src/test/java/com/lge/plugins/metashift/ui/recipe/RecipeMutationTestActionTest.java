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
import static org.junit.Assert.assertNotNull;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
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

/**
 * Unit tests for the RecipeMutationTestAction class.
 */
public class RecipeMutationTestActionTest {

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
    MetaShiftPublisher publisher = new MetaShiftPublisher("report");
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
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertNotNull(recipeAction);
    RecipeMutationTestAction action = recipeAction.getAction(RecipeMutationTestAction.class);

    assertEquals("mutation_test", action.getUrlName());

    assertEquals("16%", action.getScale());

    JSONObject metricStatistics = action.getMetricStatisticsJson();
    assertEquals(0.16, metricStatistics.getDouble("average"), 0.01);
    assertEquals(0.16, metricStatistics.getDouble("min"), 0.01);
    assertEquals(0.16, metricStatistics.getDouble("max"), 0.01);
    assertEquals(1, metricStatistics.getInt("count"));
    assertEquals(0.16, metricStatistics.getDouble("sum"), 0.01);
    assertEquals(0.16, metricStatistics.getDouble("scale"), 0.01);
    assertEquals(true, metricStatistics.getBoolean("available"));
    assertEquals(true, metricStatistics.getBoolean("percent"));

    Map [] expectedStatistics = {
      new HashMap<String, Object>() {{
        put("count", 1);
        put("width", 16);
        put("label", "Killed");
        put("clazz", "valid-good");
      }},
      new HashMap<String, Object>() {{
        put("count", 2);
        put("width", 33);
        put("label", "Survived");
        put("clazz", "valid-bad");
      }},
      new HashMap<String, Object>() {{
        put("count", 3);
        put("width", 50);
        put("label", "Skipped");
        put("clazz", "invalid");
      }}
    };
    assertEquals(JSONArray.fromObject(expectedStatistics), action.getStatistics());

    JSONArray recipeFiles = action.getTableModelJson();
    assertEquals(2, recipeFiles.getJSONObject(0).getInt("survived"));
    assertEquals(1, recipeFiles.getJSONObject(0).getInt("killed"));
    assertEquals(3, recipeFiles.getJSONObject(0).getInt("skipped"));
    
    String filePath = recipeFiles.getJSONObject(0).getString("file");
    JSONObject fileDetail = action.getFileMutationTestDetail(filePath);
    assertNotNull(fileDetail.getString("content"));
    assertEquals(6, fileDetail.getJSONArray("dataList").size());
    System.out.println(fileDetail);
  }

  @Test
  public void testStatisticsCountZero() throws Exception {
    File report = utils.getPath("workspace", "report");
    report.delete();
    FakeReportBuilder builder = new FakeReportBuilder();
    FakeRecipe fakeRecipe = new FakeRecipe(utils.getPath("path", "to", "source"));
    fakeRecipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 4, 5, 6)
            .setComplexity(10, 5, 6)
            .setCodeViolations(1, 2, 3)
            .setTests(0, 0, 0, 0)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(0, 0, 0));
    builder.add(fakeRecipe);
    builder.toFile(report);
    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);

    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertNotNull(recipeAction);
    RecipeMutationTestAction action = recipeAction.getAction(RecipeMutationTestAction.class);

    Map [] expectedStatistics = {
      new HashMap<String, Object>() {{
        put("count", 0);
        put("width", 0);
        put("label", "Killed");
        put("clazz", "valid-good");
      }},
      new HashMap<String, Object>() {{
        put("count", 0);
        put("width", 0);
        put("label", "Survived");
        put("clazz", "valid-bad");
      }},
      new HashMap<String, Object>() {{
        put("count", 0);
        put("width", 0);
        put("label", "Skipped");
        put("clazz", "invalid");
      }}
    };
    assertEquals(JSONArray.fromObject(expectedStatistics), action.getStatistics());
  }
}
