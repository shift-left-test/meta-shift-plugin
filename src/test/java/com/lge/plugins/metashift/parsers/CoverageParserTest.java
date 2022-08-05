/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CoverageData;
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
 * Unit tests for the CoverageParser class.
 *
 * @author Sung Gon Kim
 */
public class CoverageParserTest {

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
    ExecutorServiceUtils.invokeAll(new CoverageParser(new FilePath(path), dataList));
  }

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.contains(CoverageData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int i, String recipe, String file, long line, long index,
      boolean covered) {
    List<CoverageData> objects = dataList.objects(CoverageData.class).collect(Collectors.toList());
    assertEquals(recipe, objects.get(i).getName());
    assertEquals(file, objects.get(i).getFile());
    assertEquals(line, objects.get(i).getLine());
    assertEquals(index, objects.get(i).getIndex());
    assertEquals(covered, objects.get(i).isCovered());
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
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "coverage").getParentFile();
    parse(directory);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("<class filename='a.cpp'>")
        .append("  <methods>")
        .append("    <method name='func1()'>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<classes> </classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithInsufficientLineData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='.hidden.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='2'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='2'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "a.cpp", 2, 0, true);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='1'/>")
        .append("      <line branch='false' hits='0' number='10'/>")
        .append("      <line branch='true' hits='1' number='30' condition-coverage='50% (1/2)'>")
        .append("        <conds>")
        .append("          <cond block_number='0' branch_number='0' hit='1'/>")
        .append("          <cond block_number='0' branch_number='1' hit='0'/>")
        .append("        </conds>")
        .append("      </line>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 5);
    assertValues(0, "D-1.0.0-r0", "a.cpp", 1, 0, true);
    assertValues(1, "D-1.0.0-r0", "a.cpp", 10, 0, false);
    assertValues(2, "D-1.0.0-r0", "a.cpp", 30, 0, true);
    assertValues(3, "D-1.0.0-r0", "a.cpp", 30, 0, true);
    assertValues(4, "D-1.0.0-r0", "a.cpp", 30, 1, false);
  }

  @Test
  public void testCreateWithMultipleFiles() throws Exception {
    File directory = utils.createDirectory("report", "E-1.0.0-r0");
    builder
        .append("<classes>")
        .append("  <class filename='a.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='1'/>")
        .append("      <line branch='false' hits='0' number='10'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("  <class filename='b.cpp'>")
        .append("    <methods>")
        .append("      <method name='func1()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("      <method name='func2()'>")
        .append("        <lines>")
        .append("          <line number='20'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='true' hits='1' number='30' condition-coverage='50% (1/2)'>")
        .append("        <conds>")
        .append("          <cond block_number='0' branch_number='0' hit='1'/>")
        .append("          <cond block_number='0' branch_number='1' hit='0'/>")
        .append("        </conds>")
        .append("      </line>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse(directory);
    assertDataList(true, 5);
    assertValues(0, "E-1.0.0-r0", "a.cpp", 1, 0, true);
    assertValues(1, "E-1.0.0-r0", "a.cpp", 10, 0, false);
    assertValues(2, "E-1.0.0-r0", "b.cpp", 30, 0, true);
    assertValues(3, "E-1.0.0-r0", "b.cpp", 30, 0, true);
    assertValues(4, "E-1.0.0-r0", "b.cpp", 30, 1, false);
  }
}
