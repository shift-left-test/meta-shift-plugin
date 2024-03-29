/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CommentData;
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
 * Unit tests for the CommentParser class.
 *
 * @author Sung Gon Kim
 */
public class CommentParserTest {

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
    ExecutorServiceUtils.invokeAll(new CommentParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.contains(CommentData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, long lines, long commentLines) {
    List<CommentData> objects = dataList.objects(CommentData.class).collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getName());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(lines, objects.get(index).getLines());
    assertEquals(commentLines, objects.get(index).getCommentLines());
  }

  @Test
  public void testCreateSetWithUnknownPath() throws IOException, InterruptedException {
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
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTotalLines() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("{")
        .append("  'size': [")
        .append("    { 'file': 'a.file' }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoCommentLines() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 20")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': '.hidden.file',")
        .append("      'total_lines': 20,")
        .append("      'code_lines': 1,")
        .append("      'comment_lines': 5,")
        .append("      'duplicated_lines': 2,")
        .append("      'functions': 15,")
        .append("      'classes': 6")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
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
        .append("      'code_lines': 1,")
        .append("      'comment_lines': 5,")
        .append("      'duplicated_lines': 2,")
        .append("      'functions': 15,")
        .append("      'classes': 6")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertValues(0, "C-1.0.0-r0", "a.file", 20, 5);
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
        .append("      'comment_lines': 5")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'total_lines': 20,")
        .append("      'comment_lines': 3")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertValues(0, "D-1.0.0-r0", "a.file", 10, 5);
    assertValues(1, "D-1.0.0-r0", "b.file", 20, 3);
  }
}
