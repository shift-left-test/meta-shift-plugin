/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
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

  /**
   * Number of descriptors this JVM has open, or -1 where the platform does not expose it.
   * Rendering a report reads two stored objects per recipe in one uninterrupted run, so a
   * descriptor left open per read can cross the process limit before GC reclaims any of them.
   */
  private static long openDescriptors() {
    File[] descriptors = new File("/proc/self/fd").listFiles();
    return descriptors == null ? -1 : descriptors.length;
  }

  @Test
  public void testGetDoesNotLeaveTheDecompressionStreamOpen() throws IOException {
    assumeTrue("this platform does not expose /proc/self/fd", openDescriptors() >= 0);
    fileStore.put("A", "hello world".getBytes(StandardCharsets.UTF_8));
    assertNotNull(fileStore.get("A"));

    long before = openDescriptors();
    for (int i = 0; i < 300; i++) {
      assertNotNull(fileStore.get("A"));
    }
    long leaked = openDescriptors() - before;

    assertTrue("get() left " + leaked + " descriptors open over 300 reads", leaked < 20);
  }

  @Test
  public void testGetPreparedData() throws IOException, InterruptedException {
    byte[] HELLO_WORLD = "hello world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("X", HELLO_WORLD);
    fileStore.flush();

    FileStore newFileStore = new FileStore(new FilePath(storage));
    assertEquals(1, newFileStore.size());
    assertArrayEquals(HELLO_WORLD, newFileStore.get("X"));
  }

  @Test
  public void testPutDoesNotRewriteTheIndex() throws IOException {
    // Publishing a report is thousands of puts against one store, so rewriting the whole index
    // per put makes the step quadratic in the number of stored objects.
    String empty = FileUtils.readFileToString(referer, StandardCharsets.UTF_8);
    for (int i = 0; i < 50; i++) {
      fileStore.put("K" + i, ("value" + i).getBytes(StandardCharsets.UTF_8));
    }

    assertEquals("put() rewrote index.json", empty,
        FileUtils.readFileToString(referer, StandardCharsets.UTF_8));
  }

  @Test
  public void testPutIsVisibleWithinTheSameStoreBeforeFlush() throws IOException {
    byte[] HELLO_WORLD = "hello world".getBytes(StandardCharsets.UTF_8);
    fileStore.put("X", HELLO_WORLD);

    assertEquals(1, fileStore.size());
    assertTrue(fileStore.has("X"));
    assertArrayEquals(HELLO_WORLD, fileStore.get("X"));
  }

  @Test
  public void testFlushWritesEveryKeyOfTheIndex() throws IOException, InterruptedException {
    for (int i = 0; i < 50; i++) {
      fileStore.put("K" + i, ("value" + i).getBytes(StandardCharsets.UTF_8));
    }
    fileStore.flush();

    FileStore reopened = new FileStore(new FilePath(storage));
    assertEquals(50, reopened.size());
    for (int i = 0; i < 50; i++) {
      assertArrayEquals(("value" + i).getBytes(StandardCharsets.UTF_8), reopened.get("K" + i));
    }
  }

  @Test
  public void testFlushWithoutAnyPutKeepsTheStoreReadable()
      throws IOException, InterruptedException {
    fileStore.flush();
    assertEquals(0, new FileStore(new FilePath(storage)).size());
  }
}
