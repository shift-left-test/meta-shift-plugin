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

package com.lge.plugins.metashift.view;

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.models.CodeViolationList;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.CommentList;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SizeData;
import com.lge.plugins.metashift.models.SizeList;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.File;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * MetaShift post build step class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisher extends Recorder implements SimpleBuildStep {

  private final String reportRoot;
  private Criteria localCriteria;

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher(String reportRoot, Criteria localCriteria) {
    this.reportRoot = reportRoot;
    this.localCriteria = localCriteria;
  }

  public String getReportRoot() {
    return reportRoot;
  }

  public boolean isUseLocalCriteria() {
    return this.localCriteria != null;
  }

  public Criteria getLocalCriteria() {
    return this.localCriteria;
  }

  /**
   * Plugin descriptor class.
   * <p>
   * Symbol "metashift" is annotated for pipeline job simple usage. pipeline script example below.
   * metashift reportRoot:'report_test/report',  localCriteria: null
   * </p>
   */
  @Symbol("metashift")
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    private Criteria criteria;

    /**
     * Default constructor.
     */
    public DescriptorImpl() {
      super(MetaShiftPublisher.class);
      load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> clazz) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Meta Shift Report";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindJSON(this.getCriteria(), formData);
      save();

      return super.configure(req, formData);
    }

    /**
     * Returns the criteria.
     *
     * @return criteria
     */
    public Criteria getCriteria() {
      if (this.criteria == null) {
        this.criteria = new Criteria();
      }

      return this.criteria;
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    // TODO: process raw data file. ex copy report files

    // load recipe list.
    final RecipeList recipes = RecipeList.create(
        new File(new FilePath(workspace, this.reportRoot).toURI()));

    // front-end TEST dataset.
    // TODO: delete when testable data set prepared.
    Recipe myRecipe = new Recipe("A-B-C");
    myRecipe.get(SizeList.class).add(new SizeData("A-B-C", "a.file", 100, 50, 20));
    myRecipe.get(CommentList.class).add(new CommentData("A-B-C", "a.file", 100, 30));
    myRecipe.get(CodeViolationList.class).add(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 1,
            "test_rule", "test", "test description", "FATAL", "cppcheck"));
    recipes.add(myRecipe);

    // load criteria.
    Criteria criteria = (this.localCriteria == null)
        ? ((DescriptorImpl) getDescriptor()).getCriteria() : this.localCriteria;

    // create action.
    MetaShiftBuildAction buildAction = new MetaShiftBuildAction(run, criteria, recipes);
    run.addAction(buildAction);
  }
}