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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import hudson.model.FreeStyleProject;
import java.io.File;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;

/**
 * Unit tests for the MetaShiftPublisher class.
 *
 * @author Sung Gon Kim
 */
public class MetaShiftPublisherTest {

  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();
  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testConfigureRoundTripWithEmptyDirectory() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject();
    File report = folder.newFolder("path", "to", "report");
    MetaShiftPublisher before = new MetaShiftPublisher(report.getAbsolutePath(),
        new Configuration());
    project.getPublishersList().add(before);

    // HtmlUnit does not play well with JavaScript
    WebClient client = jenkins.createWebClient();
    client.getOptions().setThrowExceptionOnScriptError(false);

    jenkins.submit(client.getPage(project, "configure").getFormByName("config"));

    jenkins.assertEqualDataBoundBeans(
        new MetaShiftPublisher(report.getAbsolutePath(), new Configuration()),
        project.getPublishersList().get(0));
  }
}
