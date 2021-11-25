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
 * Unit tests for the FakeMeta class.
 *
 * @author Sung Gon Kim
 */
public class FakeMetadataTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeMetadata fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeMetadata(fakeRecipe);
    report = utils.getPath("report");
  }

  @Test
  public void testToFile() throws IOException, InterruptedException {
    fakeReport.toFile(report);
    File file = FileUtils.getFile(report, fakeRecipe.getName(), "metadata.json");
    assertTrue(file.exists());
    Any object = JsonUtils.createObject(new FilePath(file));
    assertEquals(fakeRecipe.getSourcePath().getAbsolutePath(), object.toString("S"));
  }
}
