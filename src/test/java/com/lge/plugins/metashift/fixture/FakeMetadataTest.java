/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jsoniter.any.Any;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeMetadata class.
 *
 * @author Sung Gon Kim
 */
public class FakeMetadataTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testToFileCreatesMetadataWithSourceDir() throws Exception {
    File source = folder.newFolder("source");
    File report = folder.newFolder("report");
    FakeRecipe recipe = new FakeRecipe(source, "AAA-1.0.0-r0");

    new FakeMetadata(recipe).toFile(report);

    for (String task : new String[]{"test", "coverage", "checktest"}) {
      File metadata = FileUtils.getFile(report, "AAA-1.0.0-r0", task, "metadata.json");
      assertTrue(metadata.exists());
      Any json = JsonUtils.createObject(new FilePath(metadata));
      assertEquals(recipe.getSourcePath().getAbsolutePath(), json.toString("S"));
    }
  }
}
