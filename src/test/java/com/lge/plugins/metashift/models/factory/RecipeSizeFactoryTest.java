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

import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
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
 * Unit tests for the RecipeSizeFactory class.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<RecipeSizeData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(RecipeSizeData object, String recipe, String file, long lines) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(lines, object.getLines());
  }

  @Test(expected = IOException.class)
  public void testCreateWithUnknownPath() throws IOException {
    RecipeSizeFactory.create(utils.getPath("path-to-unknown"));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoTaskDirectory() throws IOException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    RecipeSizeFactory.create(directory);
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoFile() throws IOException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkrecipe").getParentFile();
    RecipeSizeFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    RecipeSizeFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'lines_of_code' : [ { 'file': 'a.bb' } ] }");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    RecipeSizeFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'lines_of_code': [] }");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    objects = RecipeSizeFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'lines_of_code': [")
        .append("    {")
        .append("      'file': 'a.bb',")
        .append("      'code_lines': 12")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    objects = RecipeSizeFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<RecipeSizeData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "a.bb", 12);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("{")
        .append("  'lines_of_code': [")
        .append("    {")
        .append("      'file': 'a.bb',")
        .append("      'code_lines': 12")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.bb',")
        .append("      'code_lines': 34")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    objects = RecipeSizeFactory.create(directory);
    assertEquals(2, objects.size());

    Iterator<RecipeSizeData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "a.bb", 12);
    assertValues(iterator.next(), "D-1.0.0-r0", "b.bb", 34);
  }
}