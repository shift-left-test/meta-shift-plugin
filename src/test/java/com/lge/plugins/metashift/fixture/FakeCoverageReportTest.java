/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeCoverageReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCoverageReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeCoverageReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeCoverageReport(fakeRecipe);
    report = utils.getPath("report");
  }

  private void assertExists(List<String> lines, String expectedLine, long expectedCount) {
    long actualCount = lines.stream().filter(line -> line.trim().startsWith(expectedLine)).count();
    assertEquals(expectedCount, actualCount);
  }

  @Test
  public void testToFile() throws IOException {
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 5, 5, 0)
        .setStatementCoverage(1, 2)
        .setBranchCoverage(3, 4));
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "coverage", "coverage.xml");
    assertTrue(file.exists());

    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    assertExists(lines, "<line branch=\"false\"", 3);
    assertExists(lines, "<cond block_number=\"0\"", 7);
  }
}
