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

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the MutationTestList class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private MutationTestList objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder, '\'', '"');
    builder = new StringBuilder();
    objects = new MutationTestList();
  }

  private void assertValues(MutationTestData object, String recipe, String file,
      String mutatedClass, String mutatedMethod, int line, String mutator, String killingTest) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(mutatedClass, object.getMutatedClass());
    assertEquals(mutatedMethod, object.getMutatedMethod());
    assertEquals(line, object.getLine());
    assertEquals(mutator, object.getMutator());
    assertEquals(killingTest, object.getKillingTest());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    MutationTestData first = new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC");
    MutationTestData second = new KilledMutationTestData("B", "b.file", "C", "f()", 1, "AOR", "TC");
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = MutationTestList.create(utils.getPath("unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A", "checktest").getParentFile();
    objects = MutationTestList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("<mutation>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestList.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFilePath>a.cpp</sourceFilePath>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestList.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append(" ");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestList.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B");
    builder.append("<mutations></mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    objects = MutationTestList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C");
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
    objects = MutationTestList.create(directory);
    assertEquals(1, objects.size());

    Iterator<MutationTestData> iterator = objects.iterator();
    assertValues(iterator.next(), "C", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D");
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
    objects = MutationTestList.create(directory);
    assertEquals(3, objects.size());

    Iterator<MutationTestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
    assertValues(iterator.next(), "D", "path/to/b.file", "B", "func2", 2, "BOR", "test2");
    assertValues(iterator.next(), "D", "path/to/c.file", "C", "func3", 3, "COR", "test3");
  }
}
