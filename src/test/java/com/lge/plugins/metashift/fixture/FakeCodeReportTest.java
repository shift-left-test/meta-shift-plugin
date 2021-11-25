/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jsoniter.any.Any;
import com.lge.plugins.metashift.utils.JsonUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeCodeReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCodeReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeCodeReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeCodeReport(fakeRecipe);
    report = utils.getPath("report");
  }

  @Test
  public void testToFile() throws IOException, InterruptedException {
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 5, 5, 2)
        .setComplexity(5, 1, 2)
        .setCodeViolations(1, 2, 3));
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "checkcode", "sage_report.json");
    assertTrue(file.exists());

    Any object = JsonUtils.createObject(new FilePath(file));
    assertEquals(3, object.get("complexity").asList().size());
    assertEquals(1, object.get("size").asList().size());
    assertEquals(6, object.get("violations").asList().size());
  }
}
