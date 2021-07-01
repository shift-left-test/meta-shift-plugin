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
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
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

public class RecipeStatementCoverageActionTest {

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
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run,
        taskListener, config, workspace.child("report"), dataSource);

    RecipeAction recipeAction = buildAction.getActions(RecipeAction.class).stream()
        .filter(o -> o.getName().equals("autotools-project-1.0.0-r0")).findFirst().orElse(null);
    Objects.requireNonNull(recipeAction);
    RecipeStatementCoverageAction action = recipeAction
        .getAction(RecipeStatementCoverageAction.class);

    String scale = action.getScale();
    assertEquals("66%", scale);

    JSONArray statistics = action.getStatistics();
    assertEquals(JSONArray
            .fromObject("[{\"count\":8,\"width\":66,\"label\":\"Covered\",\"clazz\":\"valid-good\"},"
                + "{\"count\":4,\"width\":33,\"label\":\"UnCovered\",\"clazz\":\"invalid\"}]"),
        statistics);

    JSONArray recipeFiles = action.getRecipeFiles();
    assertContainsKey(recipeFiles.getJSONObject(0),
        "coverage", "file");

    // TODO: getFileCoverageDetail can't test
    // because we can't determine source file path to metadata.json in test env
  }
}