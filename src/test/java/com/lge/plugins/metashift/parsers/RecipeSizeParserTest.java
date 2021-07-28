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
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the RecipeSizeParser class.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeParserTest {

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
    ExecutorServiceUtils.invokeAll(new RecipeSizeParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.isAvailable(RecipeSizeData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, long lines) {
    List<RecipeSizeData> objects = dataList.objects(RecipeSizeData.class)
        .collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getRecipe());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(lines, objects.get(index).getLines());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    parse(utils.getPath("unknown-path"));
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
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkrecipe").getParentFile();
    parse(directory);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    parse(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'lines_of_code': [] }");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'lines_of_code' : [ { 'file': 'a.bb' } ] }");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    parse(directory);
    assertDataList(true, 1);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'lines_of_code': [")
        .append("    {")
        .append("      'file': '.hidden.bb',")
        .append("      'code_lines': 12")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    parse(directory);
    assertDataList(true, 0);
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
    parse(directory);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "a.bb", 12);
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
    parse(directory);
    assertDataList(true, 2);
    assertValues(0, "D-1.0.0-r0", "a.bb", 12);
    assertValues(1, "D-1.0.0-r0", "b.bb", 34);
  }
}
