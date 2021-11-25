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
 * Unit tests for the RecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class RecipeReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  RecipeReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    report = new RecipeReport(dataSource, "A-A-A");
  }

  private void assertValues(RecipeGroup group) {
    assertEquals(new JSONObject(), group.getEvaluation());
    assertEquals(new JSONObject(), group.getStatistics());
    assertEquals(new JSONObject(), group.getDistribution());
    assertEquals(new JSONArray(), group.getSummaries());
    assertEquals(new JSONArray(), group.getObjects("a.file"));
    assertEquals("", group.readFile("a.file"));
  }

  @Test
  public void testInitialStatus() {
    assertEquals(new JSONObject(), report.getLinesOfCode());
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
