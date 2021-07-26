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

import com.lge.plugins.metashift.models.CoverageData;
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
 * Unit tests for the CoverageDataParser class.
 *
 * @author Sung Gon Kim
 */
public class CoverageDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private CoverageDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new CoverageDataParser(new FilePath(directory), objects);
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
    assertEquals(available, of(objects, StatementCoverageDataParsed.class).findAny().isPresent());
    assertEquals(available, of(objects, BranchCoverageDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, CoverageData.class).count());
  }

  private void assertValues(int i, String file, long line, long index, boolean covered) {
    List<CoverageData> data = of(objects, CoverageData.class).collect(Collectors.toList());
    assertEquals(file, data.get(i).getFile());
    assertEquals(line, data.get(i).getLine());
    assertEquals(index, data.get(i).getIndex());
    assertEquals(covered, data.get(i).isCovered());
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
    utils.createDirectory(directory, "coverage");
    parse();
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws IOException, InterruptedException {
    builder.append(" ");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws IOException, InterruptedException {
    builder
        .append("<class filename='a.cpp'>")
        .append("  <methods>")
        .append("    <method name='func1()'>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws IOException, InterruptedException {
    builder.append("<classes> </classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithInsufficientLineData() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 1);
    assertValues(0, "a.cpp", 2, 0, true);
  }

  @Test
  public void testCreateWithMultipleData() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 4);
    assertValues(0, "a.cpp", 1, 0, true);
    assertValues(1, "a.cpp", 10, 0, false);
    assertValues(2, "a.cpp", 30, 0, true);
    assertValues(3, "a.cpp", 30, 1, false);
  }

  @Test
  public void testCreateWithMultipleFiles() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 4);
    assertValues(0, "a.cpp", 1, 0, true);
    assertValues(1, "a.cpp", 10, 0, false);
    assertValues(2, "b.cpp", 30, 0, true);
    assertValues(3, "b.cpp", 30, 1, false);
  }
}
