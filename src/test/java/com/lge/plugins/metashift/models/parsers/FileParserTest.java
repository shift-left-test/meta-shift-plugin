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

package com.lge.plugins.metashift.models.parsers;

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
 * Unit tests for the FileParser class.
 *
 * @author Sung Gon Kim
 */
public class FileParserTest {

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
