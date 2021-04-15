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

import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the MutationTestFactory class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestFactoryTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<MutationTestData> objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = new ArrayList<>();
  }

  private void assertValues(MutationTestData object, String recipe, String file,
      String mutatedClass, String mutatedMethod, long line, String mutator, String killingTest) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(mutatedClass, object.getMutatedClass());
    assertEquals(mutatedMethod, object.getMutatedMethod());
    assertEquals(line, object.getLine());
    assertEquals(mutator, object.getMutator());
    assertEquals(killingTest, object.getKillingTest());
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = MutationTestFactory.create(utils.getPath("path-to-unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checktest").getParentFile();
    objects = MutationTestFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("<mutation>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFilePath>a.cpp</sourceFilePath>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<mutations></mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    objects = MutationTestFactory.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>a.file</sourceFile>")
        .append("    <sourceFilePath>path/to/a.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    objects = MutationTestFactory.create(directory);
    assertEquals(1, objects.size());

    Iterator<MutationTestData> iterator = objects.iterator();
    assertValues(iterator.next(), "C-1.0.0-r0", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D-1.0.0-r0");
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>a.file</sourceFile>")
        .append("    <sourceFilePath>path/to/a.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("  <mutation detected='false'>")
        .append("    <sourceFile>b.file</sourceFile>")
        .append("    <sourceFilePath>path/to/b.file</sourceFilePath>")
        .append("    <mutatedClass>B</mutatedClass>")
        .append("    <mutatedMethod>func2</mutatedMethod>")
        .append("    <lineNumber>2</lineNumber>")
        .append("    <mutator>BOR</mutator>")
        .append("    <killingTest>test2</killingTest>")
        .append("  </mutation>")
        .append("  <mutation detected='skipped'>")
        .append("    <sourceFile>c.file</sourceFile>")
        .append("    <sourceFilePath>path/to/c.file</sourceFilePath>")
        .append("    <mutatedClass>C</mutatedClass>")
        .append("    <mutatedMethod>func3</mutatedMethod>")
        .append("    <lineNumber>3</lineNumber>")
        .append("    <mutator>COR</mutator>")
        .append("    <killingTest>test3</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    objects = MutationTestFactory.create(directory);
    assertEquals(3, objects.size());

    Iterator<MutationTestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D-1.0.0-r0", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
    assertValues(iterator.next(), "D-1.0.0-r0", "path/to/b.file", "B", "func2", 2, "BOR", "test2");
    assertValues(iterator.next(), "D-1.0.0-r0", "path/to/c.file", "C", "func3", 3, "COR", "test3");
  }
}
