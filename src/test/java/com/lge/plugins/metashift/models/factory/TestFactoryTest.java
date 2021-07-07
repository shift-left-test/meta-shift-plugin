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

package com.lge.plugins.metashift.models.factory;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the TestFactory class.
 *
 * @author Sung Gon Kim
 */
public class TestFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<TestData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(TestData object, String recipe, String suite, String name,
      String message) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(suite, object.getSuite());
    assertEquals(name, object.getName());
    assertEquals(message, object.getMessage());
  }

  @Test(expected = IOException.class)
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    TestFactory.create(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    TestFactory.create(new FilePath(directory));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "test").getParentFile();
    TestFactory.create(new FilePath(directory));
  }

  @Test
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("/testsuite>");
    utils.writeLines(builder, directory, "test", "1.xml");
    TestFactory.create(new FilePath(directory));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "test", "1.xml");
    objects = TestFactory.create(new FilePath(directory));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<testsuites> </testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    objects = TestFactory.create(new FilePath(directory));
    assertEquals(0, objects.size());
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
    TestFactory.create(new FilePath(directory));
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
    objects = TestFactory.create(new FilePath(directory));
    assertEquals(1, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "A", "test1", "");
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
    objects = TestFactory.create(new FilePath(directory));
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "A", "test1", "");
    assertValues(iterator.next(), "D-1.0.0-r0", "B", "test2", "failure");
    assertValues(iterator.next(), "D-1.0.0-r0", "C", "test3", "error");
    assertValues(iterator.next(), "D-1.0.0-r0", "D", "test4", "skipped");
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
    objects = TestFactory.create(new FilePath(directory));
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "E-1.0.0-r0", "A", "test1", "");
    assertValues(iterator.next(), "E-1.0.0-r0", "B", "test2", "failure");
    assertValues(iterator.next(), "E-1.0.0-r0", "C", "test3", "error");
    assertValues(iterator.next(), "E-1.0.0-r0", "D", "test4", "skipped");
  }
}
