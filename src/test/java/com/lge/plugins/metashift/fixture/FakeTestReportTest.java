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
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeTestReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeTestReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeTestReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeTestReport(fakeRecipe);
    report = utils.getPath("report");
  }

  private void assertExists(File directory, String prefix, int numberOfFiles) {
    File[] files = directory.listFiles((dir, name) -> name.startsWith(prefix));
    Objects.requireNonNull(files);
    assertEquals(numberOfFiles, files.length);
  }

  @Test
  public void testToFile() throws IOException {
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 5, 5, 0).setTests(1, 2, 3, 4));
    fakeReport.toFile(report);

    File root = FileUtils.getFile(report, fakeRecipe.getName(), "test");
    assertTrue(root.exists());
    assertExists(root, "passed_", 1);
    assertExists(root, "failed_", 2);
    assertExists(root, "error_", 3);
    assertExists(root, "skipped_", 4);
  }
}
