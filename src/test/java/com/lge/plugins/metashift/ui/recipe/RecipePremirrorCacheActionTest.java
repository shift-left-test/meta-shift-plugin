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

import java.io.File;
import java.util.Arrays;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.SortableItemList;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RecipePremirrorCacheActionTest {
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

    ClassLoader classLoader = getClass().getClassLoader();
    FilePath reportZip = new FilePath(new File(classLoader.getResource("report.zip").toURI()));
    workspace = new FilePath(folder.newFolder("WORKSPACE"));
    reportZip.unzip(workspace);

    config = new Configuration();
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
    RecipePremirrorCacheAction action = recipeAction.getAction(RecipePremirrorCacheAction.class);

    String scale = action.getScale();
    assertEquals("100%", scale);

    JSONArray statistics = action.getStatistics();
    assertEquals(JSONArray.fromObject("[{\"count\":100,\"width\":100,\"label\":\"Cached\",\"clazz\":\"valid-good\"},"
        + "{\"count\":0,\"width\":0,\"label\":\"Uncached\",\"clazz\":\"invalid\"}]"),
        statistics);

    JSONObject recipeCaches = action.getRecipeCaches(1, 10, new SortableItemList.SortInfo [] {});
    assertEquals(10, recipeCaches.get("last_page"));
    assertTrue(IteratorUtils.toList(recipeCaches.getJSONArray("data").getJSONObject(0).keys()).containsAll(
      Arrays.asList(
        new String [] {"signature", "available"})));
  }
}
