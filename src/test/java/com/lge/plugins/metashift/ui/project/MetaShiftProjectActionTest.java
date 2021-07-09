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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Unit tests for the MetaShiftProjectAction class.
 */
public class MetaShiftProjectActionTest {

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

  @Test
  public void testDoIndex() throws Exception {
    MetaShiftProjectAction projectAction = new MetaShiftProjectAction(project);

    StaplerRequest req = mock(StaplerRequest.class);
    StaplerResponse rsp = mock(StaplerResponse.class);

    projectAction.doIndex(req, rsp);

    verify(rsp).sendRedirect2("nodata");

    FreeStyleBuild run = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource = new DataSource(new FilePath(
        new FilePath(run.getRootDir()), "meta-shift-report"));
    FilePath reportPath = workspace.child("report");
    Recipes recipes = new Recipes(reportPath, taskListener.getLogger());
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run,
        taskListener, config, reportPath, dataSource, recipes);
    run.addAction(buildAction);

    projectAction.doIndex(req, rsp);

    verify(rsp).sendRedirect2("../../../" + buildAction.getUrl());
  }

  @Test
  public void testGetTrendChartModel() throws Exception {
    MetaShiftProjectAction projectAction = new MetaShiftProjectAction(project);

    JSONObject chartModel = projectAction.getTrendChartModel();
    assertEquals(11, chartModel.getJSONArray("legend").size());
    assertEquals(11, chartModel.getJSONArray("series").size());
    assertEquals(0, chartModel.getJSONArray("builds").size());

    FreeStyleBuild run = jenkins.buildAndAssertSuccess(project);
    DataSource dataSource = new DataSource(new FilePath(
        new FilePath(run.getRootDir()), "meta-shift-report"));
    FilePath reportPath = workspace.child("report");
    Recipes recipes = new Recipes(reportPath, taskListener.getLogger());
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run,
        taskListener, config, reportPath, dataSource, recipes);
    run.addAction(buildAction);

    JSONObject chartModel2 = projectAction.getTrendChartModel();
    assertEquals(11, chartModel2.getJSONArray("legend").size());
    assertEquals(11, chartModel2.getJSONArray("series").size());
    assertEquals(1, chartModel2.getJSONArray("builds").size());
  }
}
