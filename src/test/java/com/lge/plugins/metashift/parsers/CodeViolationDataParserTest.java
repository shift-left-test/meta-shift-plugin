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

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the CodeViolationDataParser class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private CodeViolationDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new CodeViolationDataParser(new FilePath(directory), objects);
  }

  private void parse() throws IOException, InterruptedException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      executor.submit(parser).get();
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) cause;
      }
      if (cause instanceof InterruptedException) {
        throw (InterruptedException) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new RuntimeException("Unknown exception: " + cause.getMessage(), cause);
    }
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  private static <T> Stream<T> of(List<Data> objects, Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }

  private void assertDataList(boolean available, int size) {
    assertEquals(available, of(objects, CodeViolationDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, CodeViolationData.class).count());
  }

  private void assertValues(int index, String file, long line, long column, String rule,
      String message, String description, String severity, String tool) {
    List<CodeViolationData> data = of(objects, CodeViolationData.class)
        .collect(Collectors.toList());
    assertEquals(file, data.get(index).getFile());
    assertEquals(line, data.get(index).getLine());
    assertEquals(column, data.get(index).getColumn());
    assertEquals(rule, data.get(index).getRule());
    assertEquals(message, data.get(index).getMessage());
    assertEquals(description, data.get(index).getDescription());
    assertEquals(severity, data.get(index).getSeverity());
    assertEquals(tool, data.get(index).getTool());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    directory = utils.getPath("path-to-unknown");
    parse();
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    parse();
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    utils.createDirectory(directory, "checkcode");
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws IOException, InterruptedException {
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws IOException, InterruptedException {
    builder.append("{ 'violations': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'violations': [")
        .append("    { 'file': 'a.file', 'line': 1, 'column': 100, 'rule': 'syntax' }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownLevelType() throws IOException, InterruptedException {
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
    parse();
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'violations': [")
        .append("    {")
        .append("      'file': '.hidden.file',")
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
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 1);
    assertValues(0, "a.file", 1, 100, "NPE", "NPE_message", "NPE_desc", "error", "cppcheck");
  }

  @Test
  public void testCreateWithMultipleData() throws IOException, InterruptedException {
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
    parse();
    assertValues(0, "a.file", 1, 100, "NPE", "NPE_message", "NPE_desc", "error", "cppcheck");
    assertValues(1, "b.file", 2, 200, "cast", "cast_message", "cast_desc", "warning", "cpplint");
    assertValues(2, "c.file", 3, 300, "typo", "typo_message", "typo_desc", "note", "clang-tidy");
  }
}
