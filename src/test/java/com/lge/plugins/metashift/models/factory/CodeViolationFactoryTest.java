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

import com.lge.plugins.metashift.models.CodeViolationData;
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
 * Unit tests for the CodeViolationFactory class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<CodeViolationData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(CodeViolationData object, String recipe, String file, long line,
      long column, String rule, String message, String description, String severity, String tool) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(line, object.getLine());
    assertEquals(column, object.getColumn());
    assertEquals(rule, object.getRule());
    assertEquals(message, object.getMessage());
    assertEquals(description, object.getDescription());
    assertEquals(severity, object.getSeverity());
    assertEquals(tool, object.getTool());
  }

  @Test(expected = IOException.class)
  public void testCreateWithUnknownPath() throws IOException {
    CodeViolationFactory.create(utils.getPath("path-to-unknown"));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoTaskDirectory() throws IOException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    CodeViolationFactory.create(directory);
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoFile() throws IOException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcode").getParentFile();
    CodeViolationFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    CodeViolationFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("{")
        .append("  'violations': [")
        .append("    { 'file': 'a.file', 'line': 1, 'column': 100, 'rule': 'syntax' }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    CodeViolationFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownLevelType() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("{")
        .append("  'violations': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'column': 100,")
        .append("      'rule': 'NPE',")
        .append("      'message': 'NPE_message',")
        .append("      'description': 'NPE_desc',")
        .append("      'severity': 'error',")
        .append("      'level': '???',")
        .append("      'tool': 'cppcheck'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    CodeViolationFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'violations': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = CodeViolationFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'violations': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'column': 100,")
        .append("      'rule': 'NPE',")
        .append("      'message': 'NPE_message',")
        .append("      'description': 'NPE_desc',")
        .append("      'severity': 'error',")
        .append("      'level': 'major',")
        .append("      'tool': 'cppcheck'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = CodeViolationFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<CodeViolationData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "a.file", 1, 100, "NPE", "NPE_message",
        "NPE_desc", "error", "cppcheck");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("{")
        .append("  'violations': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'column': 100,")
        .append("      'rule': 'NPE',")
        .append("      'message': 'NPE_message',")
        .append("      'description': 'NPE_desc',")
        .append("      'severity': 'error',")
        .append("      'level': 'major',")
        .append("      'tool': 'cppcheck'")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'line': 2,")
        .append("      'column': 200,")
        .append("      'rule': 'cast',")
        .append("      'message': 'cast_message',")
        .append("      'description': 'cast_desc',")
        .append("      'severity': 'warning',")
        .append("      'level': 'minor',")
        .append("      'tool': 'cpplint'")
        .append("    },")
        .append("    {")
        .append("      'file': 'c.file',")
        .append("      'line': 3,")
        .append("      'column': 300,")
        .append("      'rule': 'typo',")
        .append("      'message': 'typo_message',")
        .append("      'description': 'typo_desc',")
        .append("      'severity': 'note',")
        .append("      'level': 'info',")
        .append("      'tool': 'clang-tidy'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    objects = CodeViolationFactory.create(directory);
    assertEquals(3, objects.size());

    Iterator<CodeViolationData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "a.file", 1, 100, "NPE", "NPE_message",
        "NPE_desc", "error", "cppcheck");
    assertValues(iterator.next(), "D-1.0.0-r0", "b.file", 2, 200, "cast", "cast_message",
        "cast_desc", "warning", "cpplint");
    assertValues(iterator.next(), "D-1.0.0-r0", "c.file", 3, 300, "typo", "typo_message",
        "typo_desc", "note", "clang-tidy");
  }
}