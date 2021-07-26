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

import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.models.TestData;
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
 * Unit tests for the TestDataParser class.
 *
 * @author Sung Gon Kim
 */
public class TestDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private TestDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new TestDataParser(new FilePath(directory), objects);
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
    assertEquals(available, of(objects, TestDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, TestData.class).count());
  }

  private void assertValues(int index, String suite, String name, String message) {
    List<TestData> data = of(objects, TestData.class).collect(Collectors.toList());
    assertEquals(suite, data.get(index).getSuite());
    assertEquals(name, data.get(index).getName());
    assertEquals(message, data.get(index).getMessage());
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
    utils.createDirectory(directory, "test");
    parse();
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws IOException, InterruptedException {
    builder.append("/testsuite>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws IOException, InterruptedException {
    builder.append(" ");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws IOException, InterruptedException {
    builder.append("<testsuites> </testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse();
    assertDataList(true, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInvalidData() throws IOException, InterruptedException {
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'>")
        .append("      <invalid message='invalid'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse();
  }

  @Test
  public void testCreateWithSingleData() throws IOException, InterruptedException {
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'/>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    parse();
    assertDataList(true, 1);
    assertValues(0, "A", "test1", "");
  }

  @Test
  public void testCreateWithMultipleData() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 4);
    assertValues(0, "A", "test1", "");
    assertValues(1, "B", "test2", "failure");
    assertValues(2, "C", "test3", "error");
    assertValues(3, "D", "test4", "skipped");
  }

  @Test
  public void testCreateWithMultipleFiles() throws IOException, InterruptedException {
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
    parse();
    assertDataList(true, 4);
    assertValues(0, "A", "test1", "");
    assertValues(1, "B", "test2", "failure");
    assertValues(2, "C", "test3", "error");
    assertValues(3, "D", "test4", "skipped");
  }
}
