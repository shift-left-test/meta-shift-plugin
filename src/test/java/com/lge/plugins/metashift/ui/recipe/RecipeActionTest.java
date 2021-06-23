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
import java.util.List;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.persistence.DataSource;
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
import net.sf.json.JSONObject;

public class RecipeActionTest {
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

    List<RecipeAction> recipeActions = buildAction.getActions(RecipeAction.class);

    assertEquals(3, recipeActions.size());

    RecipeAction recipeAction = recipeActions.get(0);

    JSONObject codeSizeDeltaJson = recipeAction.getCodeSizeDeltaJson();
    assertTrue(IteratorUtils.toList(codeSizeDeltaJson.keys()).containsAll(
      Arrays.asList(
        new String [] {"functions", "classes", "files", "lines"})));

    JSONObject codeSizeJson = recipeAction.getCodeSizeJson();
    assertTrue(IteratorUtils.toList(codeSizeJson.keys() ).containsAll(
      Arrays.asList(
        new String [] {"qualified", "functions", "classes", "available", "files", "threshold",
        "lines", "denominator", "numerator", "ratio"})));

    List<String> evaluatorJsonFields = Arrays.asList(
      new String [] {"qualified", "available", "threshold", "denominator", "numerator", "ratio"});

    JSONObject premirrorCacheJson = recipeAction.getPremirrorCacheJson();
    assertTrue(IteratorUtils.toList(premirrorCacheJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject sharedStateCacheJson = recipeAction.getSharedStateCacheJson();
    assertTrue(IteratorUtils.toList(sharedStateCacheJson.keys()).containsAll(
      evaluatorJsonFields));
      
    JSONObject codeViolationsJson = recipeAction.getCodeViolationsJson();
    assertTrue(IteratorUtils.toList(codeViolationsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject commentsJson = recipeAction.getCommentsJson();
    assertTrue(IteratorUtils.toList(commentsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject complexityJson = recipeAction.getComplexityJson();
    assertTrue(IteratorUtils.toList(complexityJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject coverageJson = recipeAction.getCoverageJson();
    assertTrue(IteratorUtils.toList(coverageJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject duplicationsJson = recipeAction.getDuplicationsJson();
    assertTrue(IteratorUtils.toList(duplicationsJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject mutationTestJson = recipeAction.getMutationTestJson();
    assertTrue(IteratorUtils.toList(mutationTestJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject recipeViolationJson = recipeAction.getRecipeViolationsJson();
    assertTrue(IteratorUtils.toList(recipeViolationJson.keys()).containsAll(
      evaluatorJsonFields));

    JSONObject testJson = recipeAction.getTestJson();
    assertTrue(IteratorUtils.toList(testJson.keys()).containsAll(
      evaluatorJsonFields));
  }
}
