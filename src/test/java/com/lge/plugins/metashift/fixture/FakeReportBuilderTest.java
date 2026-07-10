/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class FakeReportBuilderTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File source;
  private File report;
  private FakeReportBuilder builder;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    source = utils.getPath("path", "to", "source");
    report = utils.getPath("report");
    builder = new FakeReportBuilder();
  }

  @Test
  public void testCreateWithNoData() throws IOException {
    builder.toFile(report);
    assertNull(report.listFiles());
  }

  @Test
  public void testCreateWithSingleRecipe() throws IOException {
    FakeRecipe recipe = new FakeRecipe(source);
    recipe
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(recipe);
    builder.toFile(report);

    File root = FileUtils.getFile(report, recipe.getName());
    assertTrue(root.exists());
    assertTrue(FileUtils.getFile(root, "checktest", "mutations.xml").exists());
    assertTrue(FileUtils.getFile(root, "checktest", "metadata.json").exists());
    assertTrue(FileUtils.getFile(root, "coverage", "coverage.xml").exists());
    assertTrue(FileUtils.getFile(root, "coverage", "metadata.json").exists());
    assertTrue(FileUtils.getFile(root, "test", "metadata.json").exists());
    assertNotNull(FileUtils.getFile(root, "test").listFiles());
  }
}
