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
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
  private List<ComplexityData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(ComplexityData object, String recipe, String file, String function,
      long start, long end, long value) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(function, object.getFunction());
    assertEquals(start, object.getStart());
    assertEquals(end, object.getEnd());
    assertEquals(value, object.getValue());
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = ComplexityFactory.create(utils.getPath("path-to-unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcode").getParentFile();
    objects = ComplexityFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'complexity': [ { 'file': 'a.file' } ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'complexity': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = ComplexityFactory.create(directory);
    assertEquals(0, objects.size());
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
    objects = ComplexityFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
  }

  @Test
  public void testCreateWithDuplicatedData() throws Exception {
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
    objects = ComplexityFactory.create(directory);
    assertEquals(3, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
    assertValues(iterator.next(), "D-1.0.0-r0", "a.file", "func1()", 15, 20, 3);
    assertValues(iterator.next(), "D-1.0.0-r0", "a.file", "func1()", 25, 30, 7);
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
    objects = ComplexityFactory.create(directory);
    assertEquals(3, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "E-1.0.0-r0", "a.file", "func1()", 5, 10, 1);
    assertValues(iterator.next(), "E-1.0.0-r0", "b.file", "func2()", 15, 20, 3);
    assertValues(iterator.next(), "E-1.0.0-r0", "c.file", "func3()", 25, 30, 7);
  }
}
