/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.TestData;
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
 * Unit tests for the TestParser class.
 *
 * @author Sung Gon Kim
 */
public class TestParserTest {

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
    ExecutorServiceUtils.invokeAll(new TestParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.contains(TestData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String suite, String name, String message) {
    List<TestData> objects = dataList.objects(TestData.class).collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getName());
    assertEquals(suite, objects.get(index).getSuite());
    assertEquals(name, objects.get(index).getTest());
    assertEquals(message, objects.get(index).getMessage());
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
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "test").getParentFile();
    parse(directory);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("/testsuite>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<testsuites> </testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInvalidData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'>")
        .append("      <invalid message='invalid'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'/>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "A", "test1", "");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'/>")
        .append("  </testsuite>")
        .append("  <testsuite name='B'>")
        .append("    <testcase name='test2'>")
        .append("      <failure message='failure'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("  <testsuite name='C'>")
        .append("    <testcase name='test3'>")
        .append("      <error message='error'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("  <testsuite name='D'>")
        .append("    <testcase name='test4'>")
        .append("      <skipped message='skipped'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse(directory);
    assertDataList(true, 4);
    assertValues(0, "D-1.0.0-r0", "A", "test1", "");
    assertValues(1, "D-1.0.0-r0", "B", "test2", "failure");
    assertValues(2, "D-1.0.0-r0", "C", "test3", "error");
    assertValues(3, "D-1.0.0-r0", "D", "test4", "skipped");
  }

  @Test
  public void testCreateWithMultipleFiles() throws Exception {
    File directory = utils.createDirectory("report", "E-1.0.0-r0");
    builder = new StringBuilder();
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'/>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    builder = new StringBuilder();
    builder
        .append("<testsuites>")
        .append("  <testsuite name='B'>")
        .append("    <testcase name='test2'>")
        .append("      <failure message='failure'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "2.xml");
    builder = new StringBuilder();
    builder
        .append("<testsuites>")
        .append("  <testsuite name='C'>")
        .append("    <testcase name='test3'>")
        .append("      <error message='error'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "C", "3.xml");
    builder = new StringBuilder();
    builder
        .append("<testsuites>")
        .append("  <testsuite name='D'>")
        .append("    <testcase name='test4'>")
        .append("      <skipped message='skipped'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "D", "4.xml");
    parse(directory);
    assertDataList(true, 4);
    assertValues(0, "E-1.0.0-r0", "A", "test1", "");
    assertValues(1, "E-1.0.0-r0", "B", "test2", "failure");
    assertValues(2, "E-1.0.0-r0", "C", "test3", "error");
    assertValues(3, "E-1.0.0-r0", "D", "test4", "skipped");
  }
}
