/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FileStore class.
 *
 * @author Sung Gon Kim
 */
public class FileStoreTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File storage;
  private File objects;
  private File referer;
  private FileStore fileStore;

  @Before
  public void setUp() throws IOException, InterruptedException {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    storage = utils.getPath("path", "to", "storage");
    fileStore = new FileStore(new FilePath(storage));
    objects = new File(storage, "objects");
    referer = new File(storage, "index.json");
  }

  @Test
  public void testInitialState() {
    assertEquals(0, fileStore.size());
    assertTrue(objects.exists());
    assertTrue(referer.exists());
  }

  @Test
  public void testGetWithUnknownKeyReturnsNull() {
    assertFalse(fileStore.has(null));
    assertFalse(fileStore.has(""));
    assertFalse(fileStore.has("unknown"));
    assertNull(fileStore.get(null));
    assertNull(fileStore.get(""));
    assertNull(fileStore.get("unknown"));
  }

  @Test
  public void testPutIdenticalDataStoresSingleFile() throws IOException {
    byte[] HELLO_WORLD = "hello world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("A", HELLO_WORLD);
    fileStore.put("B", HELLO_WORLD);
    assertEquals(2, fileStore.size());
    assertEquals(1, Objects.requireNonNull(objects.listFiles(File::isDirectory)).length);
  }

  @Test
  public void testPutDifferentDataStoresDifferentFiles() throws IOException {
    byte[] HELLO = "hello".getBytes(StandardCharsets.UTF_8);
    byte[] WORLD = "world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("A", HELLO);
    fileStore.put("B", WORLD);
    assertEquals(2, fileStore.size());
    assertEquals(2, Objects.requireNonNull(objects.listFiles(File::isDirectory)).length);
  }

  @Test
  public void testPutIdenticalKeyOverwritesPreviousOne() throws IOException {
    byte[] HELLO = "hello".getBytes(StandardCharsets.UTF_8);
    byte[] WORLD = "world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("A", HELLO);
    byte[] first = fileStore.get("A");
    fileStore.put("A", WORLD);
    byte[] second = fileStore.get("A");
    assertNotEquals(first, second);
    assertEquals(1, fileStore.size());
    assertEquals(2, Objects.requireNonNull(objects.listFiles(File::isDirectory)).length);
  }

  @Test
  public void testGetPreparedData() throws IOException, InterruptedException {
    byte[] HELLO_WORLD = "hello world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("X", HELLO_WORLD);

    FileStore newFileStore = new FileStore(new FilePath(storage));
    assertEquals(1, newFileStore.size());
    assertArrayEquals(HELLO_WORLD, newFileStore.get("X"));
  }
}
