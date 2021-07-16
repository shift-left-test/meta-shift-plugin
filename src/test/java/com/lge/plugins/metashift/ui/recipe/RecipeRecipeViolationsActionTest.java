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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Unit tests for the RecipeRecipeViolationAction class.
 */
public class RecipeRecipeViolationsActionTest {

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

  private JSONObject newJsonObject(int count, int width, String label, String clazz) {
    JSONObject object = new JSONObject();
    object.put("count", count);
    object.put("width", width);
    object.put("label", label);
    object.put("clazz", clazz);
    return object;
  }

  @Test
  public void testCreate() throws Exception {
    fakeRecipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 10, 0, 0));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);

    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertNotNull(recipeAction);
    RecipeRecipeViolationsAction action = recipeAction
        .getAction(RecipeRecipeViolationsAction.class);

    assertEquals("recipe_violations", action.getUrlName());

    assertEquals("0.60", action.getScale());

    JSONObject metricStatistics = action.getMetricStatisticsJson();
    assertEquals(0.6, metricStatistics.getDouble("average"), 0.01);
    assertEquals(0.6, metricStatistics.getDouble("min"), 0.01);
    assertEquals(0.6, metricStatistics.getDouble("max"), 0.01);
    assertEquals(1, metricStatistics.getInt("count"));
    assertEquals(0.6, metricStatistics.getDouble("sum"), 0.01);
    assertEquals(0.6, metricStatistics.getDouble("scale"), 0.01);
    assertTrue(metricStatistics.getBoolean("available"));
    assertFalse(metricStatistics.getBoolean("percent"));

    JSONArray expected = new JSONArray();
    expected.add(newJsonObject(1, 16, "Major", "major"));
    expected.add(newJsonObject(2, 33, "Minor", "minor"));
    expected.add(newJsonObject(3, 50, "Info", "informational"));
    assertEquals(expected, action.getStatistics());

    JSONArray recipeFiles = action.getTableModelJson();
    assertEquals(1, recipeFiles.getJSONObject(0).getInt("major"));
    assertEquals(2, recipeFiles.getJSONObject(0).getInt("minor"));
    assertEquals(3, recipeFiles.getJSONObject(0).getInt("info"));

    String filePath = recipeFiles.getJSONObject(0).getString("file");
    JSONObject fileDetail = action.getFileViolationDetail(filePath);
    assertNotNull(fileDetail.getString("content"));
    assertEquals(6, fileDetail.getJSONArray("dataList").size());
  }

  @Test
  public void testStatisticsCountZero() throws Exception {
    fakeRecipe
        .add(new FakeScript(20, 0, 0, 0))
        .add(new FakeSource(10, 10, 0, 0));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);

    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertNotNull(recipeAction);
    RecipeRecipeViolationsAction action = recipeAction
        .getAction(RecipeRecipeViolationsAction.class);

    JSONArray expected = new JSONArray();
    expected.add(newJsonObject(0, 0, "Major", "major"));
    expected.add(newJsonObject(0, 0, "Minor", "minor"));
    expected.add(newJsonObject(0, 0, "Info", "informational"));
    assertEquals(expected, action.getStatistics());
  }
}
