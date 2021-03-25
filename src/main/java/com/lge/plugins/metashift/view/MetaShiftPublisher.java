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

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * MetaShift post build step class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisher extends Recorder implements SimpleBuildStep {

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public MetaShiftPublisher() {
  }

  /**
   * Plugin descriptor class.
   */
  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
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
      save();

      return super.configure(req, formData);
    }
  }

  @Override
  public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
      throws InterruptedException, IOException {
    run.addAction(new MetaShiftBuildAction());
  }
}