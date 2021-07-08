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
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import java.io.File;
import java.net.URL;
import java.util.Objects;
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

  private TaskListener taskListener;
  private Configuration config;
  private FreeStyleProject project;
  private FilePath workspace;

  @Before
  public void setUp() throws Exception {
    taskListener = jenkins.createTaskListener();

    project = jenkins.createFreeStyleProject();

    URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("report.zip"));
    FilePath reportZip = new FilePath(new File(url.toURI()));
    workspace = new FilePath(folder.newFolder("WORKSPACE"));
    reportZip.unzip(workspace);

    config = new Configuration();
  }

  private void assertContainsKey(JSONObject object, String... keys) {
    for (String key : keys) {
      assertTrue("Key: " + key, object.containsKey(key));
    }
  }

  @Test
  public void testCreate() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource = new DataSource(new FilePath(
        new FilePath(run.getRootDir()), "meta-shift-report"));
    FilePath reportPath = workspace.child("report");
    Recipes recipes = new Recipes(reportPath, taskListener.getLogger());
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run,
        taskListener, config, reportPath, dataSource, recipes);

    assertEquals("meta-shift Report", buildAction.getDisplayName());
    assertEquals(3, buildAction.getRecipes().size());
    assertNotNull(buildAction.getMetrics());
    assertEquals(config, buildAction.getCriteria());
    assertEquals(JSONObject
            .fromObject("{\"series\":[{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,0]},"
                + "{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,100]},"
                + "{\"path\":\"\",\"link\":\"autotools-project-1.0.0-r0\",\"name\":\"autotools-project-1.0.0-r0\",\"value\":[181,54]},"
                + "{\"path\":\"\",\"link\":\"cmake-project-1.0.0-r0\",\"name\":\"cmake-project-1.0.0-r0\",\"value\":[202,54]},"
                + "{\"path\":\"\",\"link\":\"qmake5-project-1.0.0-r0\",\"name\":\"qmake5-project-1.0.0-r0\",\"value\":[333,54]}]}")
        , buildAction.getRecipesTreemapModel());

    JSONArray recipeTableModel = buildAction.getRecipesTableModel();
    assertArrayEquals(new String[]{
        "autotools-project-1.0.0-r0",
        "cmake-project-1.0.0-r0",
        "qmake5-project-1.0.0-r0",
    }, recipeTableModel.stream().map(o ->
        ((JSONObject) o).getString("name")).toArray());

    assertEquals(3, buildAction.getTestedRecipes());

    assertContainsKey(buildAction.getCodeSizeJson(), "qualified", "recipes", "functions",
        "classes", "available", "files", "threshold", "lines", "denominator", "numerator", "ratio");

    String[] evaluatorJsonFields = new String[]{"qualified", "available", "threshold",
        "denominator", "numerator", "ratio"};

    assertContainsKey(buildAction.getPremirrorCacheJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getSharedStateCacheJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getCodeViolationsJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getCommentsJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getComplexityJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getStatementCoverageJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getBranchCoverageJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getDuplicationsJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getMutationTestJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getRecipeViolationsJson(), evaluatorJsonFields);
    assertContainsKey(buildAction.getTestJson(), evaluatorJsonFields);
  }

  @Test
  public void testGetPreviousBuildAction() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource = new DataSource(new FilePath(
        new FilePath(run.getRootDir()), "meta-shift-report"));
    FilePath reportPath = workspace.child("report");
    Recipes recipes = new Recipes(reportPath, taskListener.getLogger());
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run,
        taskListener, config, reportPath, dataSource, recipes);
    run.addAction(buildAction);

    assertNull(buildAction.getPreviousBuildAction());

    FreeStyleBuild run2 = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource2 = new DataSource(new FilePath(
        new FilePath(run2.getRootDir()), "meta-shift-report"));
    Recipes recipes2 = new Recipes(reportPath, taskListener.getLogger());
    MetaShiftBuildAction buildAction2 = new MetaShiftBuildAction(run2,
        taskListener, config, reportPath, dataSource2, recipes2);
    run2.addAction(buildAction2);

    assertNotNull(buildAction2.getPreviousBuildAction());

    assertEquals(0.0, buildAction2.getTestedRecipesDelta(), 0);

    assertContainsKey(buildAction2.getCodeSizeDeltaJson(),
        "recipes", "functions", "classes", "files", "lines");

    assertEquals(0.0, buildAction2.getPremirrorCacheDelta(), 0);
    assertEquals(0.0, buildAction2.getSharedStateCacheDelta(), 0);
    assertEquals(0.0, buildAction2.getCodeViolationsDelta(), 0);
    assertEquals(0.0, buildAction2.getCommentsDelta(), 0);
    assertEquals(0.0, buildAction2.getComplexityDelta(), 0);
    assertEquals(0.0, buildAction2.getStatementCoverageDelta(), 0);
    assertEquals(0.0, buildAction2.getBranchCoverageDelta(), 0);
    assertEquals(0.0, buildAction2.getDuplicationsDelta(), 0);
    assertEquals(0.0, buildAction2.getMutationTestDelta(), 0);
    assertEquals(0.0, buildAction2.getRecipeViolationsDelta(), 0);
    assertEquals(0.0, buildAction2.getTestDelta(), 0);
  }
}
