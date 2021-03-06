/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the Parser class.
 *
 * @author Sung Gon Kim
 */
public class ParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private DataList dataList;
  private File report;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    dataList = new DataList();
    report = utils.createDirectory("report", "A-A-A");
  }

  private void prepareReportWith(String path) {
    builder = new StringBuilder();
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append(String.format("      'file': '%s',", path))
        .append("      'total_lines': 1,")
        .append("      'code_lines': 1,")
        .append("      'comment_lines': 1,")
        .append("      'duplicated_lines': 1,")
        .append("      'functions': 1,")
        .append("      'classes': 1")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, report, "checkcode", "sage_report.json");
  }

  private void assertDataListSize(int size)
      throws IOException, InterruptedException {
    dataList = new DataList();
    ExecutorServiceUtils.invokeAll(new CodeSizeParser(new FilePath(report), dataList));
    assertEquals(size, dataList.size());
  }

  @Test
  public void testEmptyFiles() throws IOException, InterruptedException {
    prepareReportWith(" ");
    assertDataListSize(1);
    prepareReportWith(" ");
    assertDataListSize(1);
  }

  @Test
  public void testNormalFiles() throws IOException, InterruptedException {
    prepareReportWith("abc");
    assertDataListSize(1);
    prepareReportWith("abc.file");
    assertDataListSize(1);
  }

  @Test
  public void testHiddenFiles() throws IOException, InterruptedException {
    prepareReportWith(".abc");
    assertDataListSize(0);
    prepareReportWith(".abc.file");
    assertDataListSize(0);
  }

  @Test
  public void testNormalPaths() throws IOException, InterruptedException {
    prepareReportWith("path/to/abc.file");
    assertDataListSize(1);
  }

  @Test
  public void testHiddenPaths() throws IOException, InterruptedException {
    prepareReportWith(".path/to/abc.file");
    assertDataListSize(0);
    prepareReportWith("path/.to/abc.file");
    assertDataListSize(0);
    prepareReportWith("path/to/.abc.file");
    assertDataListSize(0);
  }

  @Test
  public void testRelativePaths() throws IOException, InterruptedException {
    prepareReportWith("../path/to/a.file");
    assertDataListSize(1);
    prepareReportWith("path/to/../a.file");
    assertDataListSize(1);
  }

  @Test
  public void testDotPaths() throws IOException, InterruptedException {
    prepareReportWith("./path/to/a.file");
    assertDataListSize(1);
    prepareReportWith("path/to/./a.file");
    assertDataListSize(1);
  }

  @Test
  public void testDoubleDotHiddenPaths() throws IOException, InterruptedException {
    prepareReportWith("..path/to/a.file");
    assertDataListSize(0);
    prepareReportWith("path/..to/a.file");
    assertDataListSize(0);
    prepareReportWith("path/to/..a.file");
    assertDataListSize(0);
  }
}
