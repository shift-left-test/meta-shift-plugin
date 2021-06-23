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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.SortableItemList;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;

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

    assertEquals("Meta Shift Report", buildAction.getDisplayName());
    assertEquals(3, buildAction.getRecipes().size());
    assertNotNull(buildAction.getMetrics());
    assertEquals(config, buildAction.getCriteria());
    assertEquals(JSONObject.fromObject("{\"series\":[{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,0]},"
        + "{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,100]},"
        + "{\"path\":\"\",\"link\":\"autotools-project-1.0.0-r0\",\"name\":\"autotools-project-1.0.0-r0\",\"value\":[181,60]},"
        + "{\"path\":\"\",\"link\":\"cmake-project-1.0.0-r0\",\"name\":\"cmake-project-1.0.0-r0\",\"value\":[202,60]},"
        + "{\"path\":\"\",\"link\":\"qmake5-project-1.0.0-r0\",\"name\":\"qmake5-project-1.0.0-r0\",\"value\":[333,60]}]}")
        , buildAction.getRecipesTreemapModel());

    JSONObject recipeTableModel = buildAction.getRecipesTableModel(1, 10,
        new SortableItemList.SortInfo [] {});
    List<String> nameList = recipeTableModel.getJSONArray("data").stream().map(o ->
      ((JSONObject) o).getString("name")).collect(Collectors.toList());
    assertArrayEquals(new String [] {
      "autotools-project-1.0.0-r0",
      "cmake-project-1.0.0-r0",
      "qmake5-project-1.0.0-r0",
    }, nameList.toArray());

    assertEquals(3, buildAction.getTestedRecipes());

    JSONObject codeSizeJson = buildAction.getCodeSizeJson();
    assertTrue(IteratorUtils.toList(codeSizeJson.keys() ).containsAll(
      Arrays.asList(
        new String [] {"qualified", "recipes", "functions", "classes", "available", "files", "threshold",
        "lines", "denominator", "numerator", "ratio"})));

    List<String> evaluatorJsonFields = Arrays.asList(
      new String [] {"qualified", "available", "threshold", "denominator", "numerator", "ratio"});
    
    JSONObject premirrorCacheJson = buildAction.getPremirrorCacheJson();
    assertTrue(IteratorUtils.toList(premirrorCacheJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject sharedStateCacheJson = buildAction.getSharedStateCacheJson();
    assertTrue(IteratorUtils.toList(sharedStateCacheJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject codeViolationsJson = buildAction.getCodeViolationsJson();
    assertTrue(IteratorUtils.toList(codeViolationsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject commentsJson = buildAction.getCommentsJson();
    assertTrue(IteratorUtils.toList(commentsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject complexityJson = buildAction.getComplexityJson();
    assertTrue(IteratorUtils.toList(complexityJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject coverageJson = buildAction.getCoverageJson();
    assertTrue(IteratorUtils.toList(coverageJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject duplicationsJson = buildAction.getDuplicationsJson();
    assertTrue(IteratorUtils.toList(duplicationsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject mutationTestJson = buildAction.getMutationTestJson();
    assertTrue(IteratorUtils.toList(mutationTestJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject recipeViolationJson = buildAction.getRecipeViolationsJson();
    assertTrue(IteratorUtils.toList(recipeViolationJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject testJson = buildAction.getTestJson();
    assertTrue(IteratorUtils.toList(testJson.keys()).containsAll(
      evaluatorJsonFields));
  }

  @Test
  public void testGetPreviousBuildAction() throws Exception {
    FreeStyleBuild run = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource = new DataSource(new FilePath(
        new FilePath(run.getRootDir()), "meta-shift-report"));
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run, 
        taskListener, config, workspace.child("report"), dataSource);
    run.addAction(buildAction);

    assertNull(buildAction.getPreviousBuildAction());

    FreeStyleBuild run2 = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource2 = new DataSource(new FilePath(
        new FilePath(run2.getRootDir()), "meta-shift-report"));
    MetaShiftBuildAction buildAction2 = new MetaShiftBuildAction(run2, 
        taskListener, config, workspace.child("report"), dataSource2);
    run2.addAction(buildAction2);

    assertNotNull(buildAction2.getPreviousBuildAction());

    assertEquals(0.0, buildAction2.getTestedRecipesDelta(), 0);

    JSONObject codeSizeDeltaJson = buildAction2.getCodeSizeDeltaJson();
    assertTrue(IteratorUtils.toList(codeSizeDeltaJson.keys() ).containsAll(
      Arrays.asList(
        new String [] {"recipes", "functions", "classes", "files", "lines"})));

    assertEquals(0.0, buildAction2.getPremirrorCacheDelta(), 0);
    assertEquals(0.0, buildAction2.getSharedStateCacheDelta(), 0);
    assertEquals(0.0, buildAction2.getCodeViolationsDelta(), 0);
    assertEquals(0.0, buildAction2.getCommentsDelta(), 0);
    assertEquals(0.0, buildAction2.getComplexityDelta(), 0);
    assertEquals(0.0, buildAction2.getCoverageDelta(), 0);
    assertEquals(0.0, buildAction2.getDuplicationsDelta(), 0);
    assertEquals(0.0, buildAction2.getMutationTestDelta(), 0);
    assertEquals(0.0, buildAction2.getRecipeViolationsDelta(), 0);
    assertEquals(0.0, buildAction2.getTestDelta(), 0);
  }
}
