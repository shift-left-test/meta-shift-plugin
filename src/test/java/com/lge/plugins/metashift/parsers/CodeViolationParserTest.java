/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.DataList;
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
 * Unit tests for the CodeViolationParser class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationParserTest {

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
    ExecutorServiceUtils.invokeAll(new CodeViolationParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.contains(CodeViolationData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, long line, long column,
      String rule, String message, String description, String severity, String tool) {
    List<CodeViolationData> objects = dataList.objects(CodeViolationData.class)
        .collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getName());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(line, objects.get(index).getLine());
    assertEquals(column, objects.get(index).getColumn());
    assertEquals(rule, objects.get(index).getRule());
    assertEquals(message, objects.get(index).getMessage());
    assertEquals(description, objects.get(index).getDescription());
    assertEquals(severity, objects.get(index).getSeverity());
    assertEquals(tool, objects.get(index).getTool());
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
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checkcode").getParentFile();
    parse(directory);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'violations': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(true, 0);
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
    parse(directory);
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
    parse(directory);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
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
    parse(directory);
    assertDataList(true, 0);
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
    parse(directory);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "a.file", 1, 100, "NPE",
        "NPE_message", "NPE_desc", "error", "cppcheck");
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
    parse(directory);
    assertValues(0, "D-1.0.0-r0", "a.file", 1, 100, "NPE", "NPE_message",
        "NPE_desc", "error", "cppcheck");
    assertValues(1, "D-1.0.0-r0", "b.file", 2, 200, "cast", "cast_message",
        "cast_desc", "warning", "cpplint");
    assertValues(2, "D-1.0.0-r0", "c.file", 3, 300, "typo", "typo_message",
        "typo_desc", "note", "clang-tidy");
  }
}
