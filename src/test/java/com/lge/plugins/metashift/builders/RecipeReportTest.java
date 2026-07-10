/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
  private DataSource dataSource;
  RecipeReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    dataSource = new DataSource(new FilePath(folder.newFolder()));
    report = new RecipeReport(dataSource, "A-A-A");
  }

  private void assertValues(RecipeGroup group) {
    assertEquals(new JSONObject(), group.getEvaluation());
    assertEquals(new JSONObject(), group.getDistribution());
    assertEquals(new JSONArray(), group.getSummaries());
    assertEquals(new JSONArray(), group.getObjects("a.file"));
  }

  @Test
  public void testInitialStatusOfMetrics() {
    assertValues(report.getUnitTests());
    assertValues(report.getStatementCoverage());
    assertValues(report.getBranchCoverage());
    assertValues(report.getMutationTests());
  }

  @Test
  public void testReadFileReturnsStoredSource() throws IOException {
    dataSource.put("hello\nworld", "RECIPE", "NONE", "FILE", "A-A-A", "a.cpp");
    assertEquals("hello\nworld", report.readFile("a.cpp"));
    assertTrue(report.hasFile("a.cpp"));
  }

  @Test
  public void testReadFileReturnsEmptyWhenMissing() {
    assertEquals("", report.readFile("unknown.cpp"));
    assertFalse(report.hasFile("unknown.cpp"));
  }
}
