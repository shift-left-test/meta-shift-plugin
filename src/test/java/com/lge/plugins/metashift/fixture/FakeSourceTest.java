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
 * Unit tests for the FakeSource class.
 *
 * @author Sung Gon Kim
 */
public class FakeSourceTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File source;
  private FakeRecipe fakeRecipe;
  private FakeSource fakeSource;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    source = utils.getPath("path", "to", "source");
    fakeRecipe = new FakeRecipe(source);
    fakeSource = new FakeSource(fakeRecipe, 10, 2, 3, 4);
  }

  @Test
  public void testInitialState() {
    assertEquals(10, fakeSource.getTotalLines());
    assertEquals(2, fakeSource.getCodeLines());
    assertEquals(3, fakeSource.getCommentLines());
    assertEquals(4, fakeSource.getDuplicatedLines());
  }

  @Test
  public void testSetValues() {
    fakeSource.setTests(1, 2, 3, 4);
    assertEquals(1, fakeSource.getTestPassed());
    assertEquals(2, fakeSource.getTestFailed());
    assertEquals(3, fakeSource.getTestError());
    assertEquals(4, fakeSource.getTestSkipped());
  }

  @Test
  public void testToFile() throws IOException {
    fakeSource.toFile();
    File file = FileUtils.getFile(source, fakeRecipe.getName(), fakeSource.getFilename());
    assertTrue(file.exists());
    assertEquals(fakeSource.getTotalLines(),
        FileUtils.readLines(file, StandardCharsets.UTF_8).size());
  }
}
