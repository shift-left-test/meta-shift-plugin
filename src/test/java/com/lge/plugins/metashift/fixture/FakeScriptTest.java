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
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeScript class.
 *
 * @author Sung Gon Kim
 */
public class FakeScriptTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File source;
  private FakeRecipe fakeRecipe;
  private FakeScript fakeSource;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    source = utils.getPath("path", "to", "source");
    fakeRecipe = new FakeRecipe(source);
    fakeSource = new FakeScript(fakeRecipe, 10);
  }

  @Test
  public void testInitialState() {
    assertEquals(10, fakeSource.getLines());
    assertEquals(0, fakeSource.getMajorIssues());
    assertEquals(0, fakeSource.getMinorIssues());
    assertEquals(0, fakeSource.getInfoIssues());
  }

  @Test
  public void testSetValues() {
    fakeSource.setIssues(1, 2, 3);
    assertEquals(1, fakeSource.getMajorIssues());
    assertEquals(2, fakeSource.getMinorIssues());
    assertEquals(3, fakeSource.getInfoIssues());
  }

  @Test
  public void testToFile() throws IOException {
    fakeSource.toFile();
    File file = FileUtils.getFile(source, fakeRecipe.getName(), fakeSource.getFilename());
    assertTrue(file.exists());
    assertEquals(fakeSource.getLines(), FileUtils.readLines(file, StandardCharsets.UTF_8).size());
  }
}
