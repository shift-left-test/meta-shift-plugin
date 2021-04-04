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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ComplexityList class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private ComplexityList objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder, '\'', '"');
    builder = new StringBuilder();
    objects = new ComplexityList();
  }

  private void assertValues(ComplexityData object, String recipe, String file, String function,
      int start, int end, int value) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(function, object.getFunction());
    assertEquals(start, object.getStart());
    assertEquals(end, object.getEnd());
    assertEquals(value, object.getValue());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    ComplexityData first = new ComplexityData("A", "a.file", "f()", 5, 10, 1);
    ComplexityData second = new ComplexityData("B", "b.file", "g()", 5, 10, 1);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = ComplexityList.create(utils.getPath("unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A", "checkcode").getParentFile();
    objects = ComplexityList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityList.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("{ 'complexity': [ { 'file': 'a.file' } ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    ComplexityList.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B");
    builder.append("{ 'complexity': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = ComplexityList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C");
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
    objects = ComplexityList.create(directory);
    assertEquals(1, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "C", "a.file", "func1()", 5, 10, 1);
  }

  @Test
  public void testCreateWithDuplicatedData() throws Exception {
    File directory = utils.createDirectory("report", "D");
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
    objects = ComplexityList.create(directory);
    assertEquals(1, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "a.file", "func1()", 25, 30, 7);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "E");
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
    objects = ComplexityList.create(directory);
    assertEquals(3, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "E", "a.file", "func1()", 5, 10, 1);
    assertValues(iterator.next(), "E", "b.file", "func2()", 15, 20, 3);
    assertValues(iterator.next(), "E", "c.file", "func3()", 25, 30, 7);
  }
}
