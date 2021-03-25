/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.models;

import java.io.*;
import java.util.*;
import org.apache.commons.io.*;
import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheList class.
 *
 * @author Sung Gon Kim
 */
public class CacheListTest {
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private CacheList objects;

  @Before
  public void setUp() throws Exception {
    objects = new CacheList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(CacheData object, String recipe, String signature, boolean available) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(signature, object.getSignature());
    assertEquals(available, object.isAvailable());
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() throws Exception {
    CacheData first = new SharedStateCacheData("A", "X:do_compile", true);
    CacheData second = new PremirrorCacheData("A", "Y", false);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateSetWithUnknownPath() throws Exception {
    objects = CacheList.create("A", new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSetWithMalformedFile() throws Exception {
    List<String> data = Arrays.asList("{ \"Premirror\": { }, \"Shared State\": { } }");
    File file = createTempFile("report/A/caches.json", data);
    objects = CacheList.create("A", file.getParentFile());
  }

  @Test
  public void testCreateSetWithEmptyData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"Premirror\": { \"Found\": [], \"Missed\": [] },",
        "  \"Shared State\": { \"Found\": [], \"Missed\": [] } }");
    File file = createTempFile("report/A/caches.json", data);
    objects = CacheList.create("A", file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateSetWithPremirrorData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"Premirror\": { \"Found\": [\"A\", \"B\"], \"Missed\": [\"C\"] },",
        "  \"Shared State\": { \"Found\": [], \"Missed\": [] } }");
    File file = createTempFile("report/A/caches.json", data);
    objects = CacheList.create("A", file.getParentFile());
    assertEquals(3, objects.size());
  }

  @Test
  public void testCreateSetWithSharedStateData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"Premirror\": { \"Found\": [], \"Missed\": [] },",
        "  \"Shared State\": { \"Found\": [\"D:do_X\"], \"Missed\": [\"E:do_X\"] } }");
    File file = createTempFile("report/A/caches.json", data);
    objects = CacheList.create("A", file.getParentFile());
    assertEquals(2, objects.size());
  }

  @Test
  public void testCreateSetWithCompoundData() throws Exception {
    List<String> data = Arrays.asList(
      "{ \"Premirror\": { \"Found\": [\"A\", \"B\"], \"Missed\": [\"C\"] },",
      "  \"Shared State\": { \"Found\": [\"D:do_X\"], \"Missed\": [\"E:do_X\"] } }");
    File file = createTempFile("report/A/caches.json", data);
    objects = CacheList.create("A", file.getParentFile());
    assertEquals(5, objects.size());
  }
}
