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
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeRecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipeReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeRecipeReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeRecipe.add(new FakeScript(fakeRecipe, 10, 1, 2, 3));
    fakeRecipe.add(new FakeScript(fakeRecipe, 20, 3, 2, 1));
    fakeReport = new FakeRecipeReport(fakeRecipe);
    report = utils.getPath("report");
  }

  @Test
  public void testToFileCreatesFilesJson() throws IOException, InterruptedException {
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "checkrecipe", "files.json");
    assertTrue(file.exists());

    List<FakeScript> scripts = fakeRecipe.getScripts();
    Any object = JsonUtils.createObject(new FilePath(file));

    Any first = object.get("lines_of_code").asList().get(0);
    File aFile = FileUtils.getFile(fakeRecipe.getSourcePath(), scripts.get(0).getFilename());
    assertEquals(aFile.getAbsolutePath(), first.toString("file"));
    assertEquals(10, first.toLong("code_lines"));

    Any second = object.get("lines_of_code").asList().get(1);
    File bFile = FileUtils.getFile(fakeRecipe.getSourcePath(), scripts.get(1).getFilename());
    assertEquals(bFile.getAbsolutePath(), second.toString("file"));
    assertEquals(20, second.toLong("code_lines"));
  }

  @Test
  public void testToFile() throws IOException, InterruptedException {
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(),
        "checkrecipe", "recipe_violations.json");
    assertTrue(file.exists());

    Any object = JsonUtils.createObject(new FilePath(file));
    assertEquals(12, object.get("issues").asList().size());
  }
}
