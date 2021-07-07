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

package com.lge.plugins.metashift.models.factory;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the DuplicationFactory class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<DuplicationData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(DuplicationData object, String recipe, String file, long lines,
      long duplicatedLines) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(lines, object.getLines());
    assertEquals(duplicatedLines, object.getDuplicatedLines());
  }

  @Test(expected = IOException.class)
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    DuplicationFactory.create(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    DuplicationFactory.create(new FilePath(directory));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcode").getParentFile();
    DuplicationFactory.create(new FilePath(directory));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    DuplicationFactory.create(new FilePath(directory));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'size': [ { 'file': 'a.file' } ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    DuplicationFactory.create(new FilePath(directory));
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = DuplicationFactory.create(new FilePath(directory));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 20,")
        .append("      'duplicated_lines': 2")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = DuplicationFactory.create(new FilePath(directory));
    assertEquals(1, objects.size());

    DuplicationData object = objects.iterator().next();
    assertValues(object, "C-1.0.0-r0", "a.file", 20, 2);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 10,")
        .append("      'duplicated_lines': 5")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'total_lines': 20,")
        .append("      'duplicated_lines': 5")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = DuplicationFactory.create(new FilePath(directory));
    assertEquals(2, objects.size());

    Iterator<DuplicationData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "a.file", 10, 5);
    assertValues(iterator.next(), "D-1.0.0-r0", "b.file", 20, 5);
  }
}
