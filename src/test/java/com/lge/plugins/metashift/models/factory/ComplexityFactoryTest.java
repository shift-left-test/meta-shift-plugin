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

import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.DataList;
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
 * Unit tests for the ComplexityFactory class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityFactoryTest {

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

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.isAvailable(ComplexityData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, String function, long start,
      long end, long value) {
    List<ComplexityData> objects = dataList.objects(ComplexityData.class)
        .collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getRecipe());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(function, objects.get(index).getFunction());
    assertEquals(start, objects.get(index).getStart());
    assertEquals(end, objects.get(index).getEnd());
    assertEquals(value, objects.get(index).getValue());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    ComplexityFactory.create(new FilePath(utils.getPath("path-to-unknown")), dataList);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcode").getParentFile();
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'complexity': [ { 'file': 'a.file' } ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'complexity': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': '.hidden.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
  }

  @Test
  public void testCreateWithOverlappedData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    },")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 15,")
        .append("      'end': 20,")
        .append("      'value': 3")
        .append("    },")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 25,")
        .append("      'end': 30,")
        .append("      'value': 7")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 3);
    assertValues(0, "D-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
    assertValues(1, "D-1.0.0-r0", "a.file", "func1()", 15, 20, 3);
    assertValues(2, "D-1.0.0-r0", "a.file", "func1()", 25, 30, 7);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "E-1.0.0-r0");
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'function': 'func2()',")
        .append("      'start': 15,")
        .append("      'end': 20,")
        .append("      'value': 3")
        .append("    },")
        .append("    {")
        .append("      'file': 'c.file',")
        .append("      'function': 'func3()',")
        .append("      'start': 25,")
        .append("      'end': 30,")
        .append("      'value': 7")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 3);
    assertValues(0, "E-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
    assertValues(1, "E-1.0.0-r0", "b.file", "func2()", 15, 20, 3);
    assertValues(2, "E-1.0.0-r0", "c.file", "func3()", 25, 30, 7);
  }
}
