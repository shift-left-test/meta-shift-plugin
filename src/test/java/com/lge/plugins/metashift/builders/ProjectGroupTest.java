/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
