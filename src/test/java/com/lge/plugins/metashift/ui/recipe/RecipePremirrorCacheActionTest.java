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
 * Unit tests for the RecipePremirrorCacheAction class.
 */
public class RecipePremirrorCacheActionTest {

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
    fakeRecipe.setSharedState(0, 0)
        .add(new FakeScript(20, 0, 0, 0))
        .add(new FakeSource(10, 10, 0, 0));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.UNSTABLE, project);

    MetaShiftBuildAction buildAction = run.getAction(MetaShiftBuildAction.class);
    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);
    assertNotNull(recipeAction);
    RecipePremirrorCacheAction action = recipeAction.getAction(RecipePremirrorCacheAction.class);

    assertEquals("premirror_cache", action.getUrlName());

    assertEquals("0%", action.getScale());

    JSONObject metricStatistics = action.getMetricStatisticsJson();
    assertEquals(0.0, metricStatistics.getDouble("average"), 0.01);
    assertEquals(0.0, metricStatistics.getDouble("min"), 0.01);
    assertEquals(0.0, metricStatistics.getDouble("max"), 0.01);
    assertEquals(1, metricStatistics.getInt("count"));
    assertEquals(0.0, metricStatistics.getDouble("sum"), 0.01);
    assertEquals(0.0, metricStatistics.getDouble("scale"), 0.01);
    assertTrue(metricStatistics.getBoolean("available"));
    assertTrue(metricStatistics.getBoolean("percent"));

    JSONArray expected = new JSONArray();
    expected.add(newJsonObject(0, 0, "Hits", "valid-good"));
    expected.add(newJsonObject(0, 100, "Misses", "invalid"));
    assertEquals(expected, action.getStatistics());

    JSONArray recipeCaches = action.getTableModelJson();
    assertEquals(0, recipeCaches.size());
  }
}
