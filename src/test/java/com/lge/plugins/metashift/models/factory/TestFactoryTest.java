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

import com.lge.plugins.metashift.models.TemporaryFileUtils;
import com.lge.plugins.metashift.models.TestData;
import java.io.File;
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
    utils = new TemporaryFileUtils(folder, '\'', '"');
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

  @Test
  public void testCreateWithUnknownPath() {
    objects = TestFactory.create(utils.getPath("unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A", "test").getParentFile();
    objects = TestFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("/testsuite>");
    utils.writeLines(builder, directory, "test", "1.xml");
    TestFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append(" ");
    utils.writeLines(builder, directory, "test", "1.xml");
    TestFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B");
    builder.append("<testsuites> </testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    objects = TestFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInvalidData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'>")
        .append("      <invalid message='invalid'/>")
        .append("    </testcase>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    TestFactory.create(directory);
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C");
    builder
        .append("<testsuites>")
        .append("  <testsuite name='A'>")
        .append("    <testcase name='test1'/>")
        .append("  </testsuite>")
        .append("</testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    objects = TestFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "C", "A", "test1", "");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D");
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
    objects = TestFactory.create(directory);
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "A", "test1", "");
    assertValues(iterator.next(), "D", "B", "test2", "failure");
    assertValues(iterator.next(), "D", "C", "test3", "error");
    assertValues(iterator.next(), "D", "D", "test4", "skipped");
  }

  @Test
  public void testCreateWithMultipleFiles() throws Exception {
    File directory = utils.createDirectory("report", "E");
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
    objects = TestFactory.create(directory);
    assertEquals(4, objects.size());

    Iterator<TestData> iterator = objects.iterator();
    assertValues(iterator.next(), "E", "A", "test1", "");
    assertValues(iterator.next(), "E", "B", "test2", "failure");
    assertValues(iterator.next(), "E", "C", "test3", "error");
    assertValues(iterator.next(), "E", "D", "test4", "skipped");
  }
}
