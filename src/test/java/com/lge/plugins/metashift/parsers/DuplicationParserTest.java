/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the DuplicationParser class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationParserTest {

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
    ExecutorServiceUtils.invokeAll(new DuplicationParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.contains(DuplicationData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, long lines,
      long duplicatedLines) {
    List<DuplicationData> objects = dataList.objects(DuplicationData.class)
        .sorted(Comparator.comparing(DuplicationData::getFile))
        .collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getName());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(lines, objects.get(index).getLines());
    assertEquals(duplicatedLines, objects.get(index).getDuplicatedLines());
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
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [ ], 'duplications': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("{ 'size': [ { 'file': 'a.file' } ], 'duplications': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(true, 0);
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
        .append("      'duplicated_lines': 2")
        .append("    }")
        .append("  ],")
        .append("  'duplications': [")
        .append("    [")
        .append("      { 'file': '.hidden.file', 'start': 0, 'end': 10 },")
        .append("      { 'file': '.hidden.file', 'start': 10, 'end': 20 }")
        .append("    ]")
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
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 20,")
        .append("      'duplicated_lines': 2")
        .append("    }")
        .append("  ],")
        .append("  'duplications': [")
        .append("    [")
        .append("      { 'file': 'a.file', 'start': 0, 'end': 2 },")
        .append("      { 'file': 'a.file', 'start': 10, 'end': 12 }")
        .append("    ]")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(true, 2);
    assertValues(0, "C-1.0.0-r0", "a.file", 20, 2);
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
        .append("  ],")
        .append("  'duplications': [")
        .append("    [")
        .append("      { 'file': 'a.file', 'start': 0, 'end': 5 },")
        .append("      { 'file': 'a.file', 'start': 5, 'end': 10 }")
        .append("    ],")
        .append("    [")
        .append("      { 'file': 'b.file', 'start': 0, 'end': 5 },")
        .append("      { 'file': 'b.file', 'start': 15, 'end': 20 }")
        .append("    ]")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse(directory);
    assertDataList(true, 4);
    assertValues(0, "D-1.0.0-r0", "a.file", 10, 5);
    assertValues(2, "D-1.0.0-r0", "b.file", 20, 5);
  }
}
