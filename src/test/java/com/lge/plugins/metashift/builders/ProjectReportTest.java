/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;

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
 * Unit tests for the ProjectReport class.
 *
 * @author Sung Gon Kim
 */
public class ProjectReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  ProjectReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    report = new ProjectReport(dataSource);
  }

  private void assertValues(ProjectGroup group) {
    assertEquals(new JSONObject(), group.getEvaluation());
    assertEquals(new JSONObject(), group.getStatistics());
    assertEquals(new JSONObject(), group.getDistribution());
    assertEquals(new JSONArray(), group.getTreemap());
    assertEquals(new JSONArray(), group.getSummaries());
  }

  @Test
  public void testInitialStatus() {
    assertEquals(new JSONObject(), report.getLinesOfCode());
    assertEquals(new JSONObject(), report.getTestedRecipes());
    assertEquals(new JSONArray(), report.getTreemap());
    assertEquals(new JSONArray(), report.getSummaries());
  }

  @Test
  public void testInitialStatusOfMetrics() {
    assertValues(report.getPremirrorCache());
    assertValues(report.getSharedStateCache());
    assertValues(report.getRecipeViolations());
    assertValues(report.getComments());
    assertValues(report.getCodeViolations());
    assertValues(report.getComplexity());
    assertValues(report.getDuplications());
    assertValues(report.getUnitTests());
    assertValues(report.getStatementCoverage());
    assertValues(report.getBranchCoverage());
    assertValues(report.getMutationTests());
  }
}
