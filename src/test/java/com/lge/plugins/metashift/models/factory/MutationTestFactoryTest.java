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

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.MutationTestData;
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
 * Unit tests for the MutationTestFactory class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestFactoryTest {

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

  private void assertDataList(boolean isAvailable, int size) {
    assertEquals(isAvailable, dataList.isAvailable(MutationTestData.class));
    assertEquals(size, dataList.size());
  }

  private void assertValues(int index, String recipe, String file, String mutatedClass,
      String mutatedMethod, long line, String mutator, String killingTest) {
    List<MutationTestData> objects = dataList.objects(MutationTestData.class)
        .collect(Collectors.toList());
    assertEquals(recipe, objects.get(index).getRecipe());
    assertEquals(file, objects.get(index).getFile());
    assertEquals(mutatedClass, objects.get(index).getMutatedClass());
    assertEquals(mutatedMethod, objects.get(index).getMutatedMethod());
    assertEquals(line, objects.get(index).getLine());
    assertEquals(mutator, objects.get(index).getMutator());
    assertEquals(killingTest, objects.get(index).getKillingTest());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    MutationTestFactory.create(new FilePath(utils.getPath("path-to-unknown")), dataList);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    MutationTestFactory.create(new FilePath(directory), dataList);
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "A-1.0.0-r0", "checktest").getParentFile();
    MutationTestFactory.create(new FilePath(directory), dataList);
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append("<mutation>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(new FilePath(directory), dataList);
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
    MutationTestFactory.create(new FilePath(directory), dataList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    File directory = utils.createDirectory("report", "A-1.0.0-r0");
    builder.append(" ");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(new FilePath(directory), dataList);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<mutations></mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    MutationTestFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 0);
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
    MutationTestFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 1);
    assertValues(0, "C-1.0.0-r0", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
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
    MutationTestFactory.create(new FilePath(directory), dataList);
    assertDataList(true, 3);
    assertValues(0, "D-1.0.0-r0", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
    assertValues(1, "D-1.0.0-r0", "path/to/b.file", "B", "func2", 2, "BOR", "test2");
    assertValues(2, "D-1.0.0-r0", "path/to/c.file", "C", "func3", 3, "COR", "test3");
  }
}
