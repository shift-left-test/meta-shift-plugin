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

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.FilePath;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ProjectGroup class.
 *
 * @author Sung Gon Kim
 */
public class ProjectGroupTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private ProjectGroup group;

  @Before
  public void setUp() throws IOException, InterruptedException {
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    group = new ProjectGroup(dataSource, Metric.PREMIRROR_CACHE);
  }

  @Test
  public void testInitialStatus() {
    assertEquals(new JSONObject(), group.getEvaluation());
    assertEquals(new JSONObject(), group.getStatistics());
    assertEquals(new JSONObject(), group.getDistribution());
    assertEquals(new JSONArray(), group.getTreemap());
    assertEquals(new JSONArray(), group.getSummaries());
  }
}
