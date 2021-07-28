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

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the PremirrorCacheParser class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private DataList dataList;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    dataList = new DataList();
  }

  private void parse(File path) throws IOException, InterruptedException {
    ExecutorServiceUtils.invokeAll(new PremirrorCacheParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.isAvailable(PremirrorCacheData.class));
    assertEquals(size, dataList.size());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    parse(utils.getPath("path-to-unknown"));
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcache").getParentFile();
    parse(directory);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { },")
        .append("  'Shared State': { }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { 'Found': [], 'Missed': [] },")
        .append("  'Shared State': { 'Found': [], 'Missed': [] }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithPremirrorData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { 'Found': ['A', 'B'], 'Missed': ['C', 'D'] },")
        .append("  'Shared State': { 'Found': [], 'Missed': [] }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
    assertDataList(true, 4);
  }

  @Test
  public void testCreateWithSharedStateData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { 'Found': [], 'Missed': [] },")
        .append("  'Shared State': { 'Found': ['D:do_X'], 'Missed': ['E:do_X'] }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "E-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { 'Found': ['A', 'B'], 'Missed': ['C', 'D'] },")
        .append("  'Shared State': { 'Found': ['D:do_X'], 'Missed': ['E:do_X'] }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    parse(directory);
    assertDataList(true, 4);
  }
}
