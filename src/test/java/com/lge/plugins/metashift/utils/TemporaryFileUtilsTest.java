/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the TemporaryFileUtilsTest class.
 *
 * @author Sung Gon Kim
 */
public class TemporaryFileUtilsTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
  }

  @Test
  public void testGetPath() {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.getPath("A", "B", "C");
    assertFalse(actual.exists());
    assertEquals(expected, actual);
  }

  @Test
  public void testCreateDirectory() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.createDirectory("A", "B", "C");
    assertTrue(actual.exists());
    assertTrue(actual.isDirectory());
    assertEquals(expected, actual);
  }

  @Test
  public void testCreateDirectoryWithParentFile() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.createDirectory(folder.getRoot(), "A", "B", "C");
    assertTrue(actual.exists());
    assertTrue(actual.isDirectory());
    assertEquals(expected, actual);
  }

  @Test
  public void testCreateFile() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.createFile("A", "B", "C");
    assertTrue(actual.exists());
    assertTrue(actual.isFile());
    assertEquals(expected, actual);
  }

  @Test
  public void testCreateFileWithParentFile() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.createFile(folder.getRoot(), "A", "B", "C");
    assertTrue(actual.exists());
    assertTrue(actual.isFile());
    assertEquals(expected, actual);
  }

  @Test
  public void testWriteLines() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.getPath("A", "B", "C");
    utils.writeLines(new StringBuilder("TEST='A'"), actual);
    assertTrue(actual.exists());
    assertTrue(actual.isFile());
    assertEquals(expected, actual);
    assertEquals("TEST=\"A\"", FileUtils.readFileToString(actual, StandardCharsets.UTF_8));
  }

  @Test
  public void testWriteLinesWithParentFile() throws IOException {
    File expected = FileUtils.getFile(folder.getRoot(), "A", "B", "C");
    File actual = utils.getPath("A", "B", "C");
    utils.writeLines(new StringBuilder("TEST='A'"), folder.getRoot(), "A", "B", "C");
    assertTrue(actual.exists());
    assertTrue(actual.isFile());
    assertEquals(expected, actual);
    assertEquals("TEST=\"A\"", FileUtils.readFileToString(actual, StandardCharsets.UTF_8));
  }
}
