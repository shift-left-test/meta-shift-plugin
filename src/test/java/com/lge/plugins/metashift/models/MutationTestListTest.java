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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
  private MutationTestList objects;

  @Before
  public void setUp() {
    objects = new MutationTestList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
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
    objects = MutationTestList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File file = folder.newFolder("report/A/checktest");
    objects = MutationTestList.create(file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    List<String> data = Arrays.asList(
        "<mutation",
        "mutation/>"
    );
    File file = createTempFile("report/A/checktest/mutations.xml", data);
    objects = MutationTestList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    List<String> data = Arrays.asList(
        "<mutations>",
        "  <mutation detected=\"true\">",
        "    <sourceFile>a.cpp</sourceFile>",
        "  </mutation>",
        "</mutations>"
    );
    File file = createTempFile("report/A/checktest/mutations.xml", data);
    objects = MutationTestList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws Exception {
    List<String> data = Collections.singletonList("");
    File file = createTempFile("report/B/checktest/mutations.xml", data);
    objects = MutationTestList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    List<String> data = Arrays.asList(
        "<mutations>",
        "</mutations>"
    );
    File file = createTempFile("report/B/checktest/mutations.xml", data);
    objects = MutationTestList.create(file.getParentFile().getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    List<String> data = Arrays.asList(
        "<mutations>",
        "  <mutation detected=\"true\">",
        "    <sourceFile>a.file</sourceFile>",
        "    <sourceFilePath>path/to/a.file</sourceFilePath>",
        "    <mutatedClass>A</mutatedClass>",
        "    <mutatedMethod>func1</mutatedMethod>",
        "    <lineNumber>1</lineNumber>",
        "    <mutator>AOR</mutator>",
        "    <killingTest>test1</killingTest>",
        "  </mutation>",
        "</mutations>"
    );
    File file = createTempFile("report/C/checktest/mutations.xml", data);

    objects = MutationTestList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    MutationTestData object = objects.iterator().next();
    assertValues(object, "C", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    List<String> data = Arrays.asList(
        "<mutations>",
        "  <mutation detected=\"true\">",
        "    <sourceFile>a.file</sourceFile>",
        "    <sourceFilePath>path/to/a.file</sourceFilePath>",
        "    <mutatedClass>A</mutatedClass>",
        "    <mutatedMethod>func1</mutatedMethod>",
        "    <lineNumber>1</lineNumber>",
        "    <mutator>AOR</mutator>",
        "    <killingTest>test1</killingTest>",
        "  </mutation>",
        "  <mutation detected=\"false\">",
        "    <sourceFile>b.file</sourceFile>",
        "    <sourceFilePath>path/to/b.file</sourceFilePath>",
        "    <mutatedClass>B</mutatedClass>",
        "    <mutatedMethod>func2</mutatedMethod>",
        "    <lineNumber>2</lineNumber>",
        "    <mutator>BOR</mutator>",
        "    <killingTest>test2</killingTest>",
        "  </mutation>",
        "  <mutation detected=\"skipped\">",
        "    <sourceFile>c.file</sourceFile>",
        "    <sourceFilePath>path/to/c.file</sourceFilePath>",
        "    <mutatedClass>C</mutatedClass>",
        "    <mutatedMethod>func3</mutatedMethod>",
        "    <lineNumber>3</lineNumber>",
        "    <mutator>COR</mutator>",
        "    <killingTest>test3</killingTest>",
        "  </mutation>",
        "</mutations>"
    );
    File file = createTempFile("report/D/checktest/mutations.xml", data);

    objects = MutationTestList.create(file.getParentFile().getParentFile());
    assertEquals(3, objects.size());

    Iterator<MutationTestData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "path/to/a.file", "A", "func1", 1, "AOR", "test1");
    assertValues(iterator.next(), "D", "path/to/b.file", "B", "func2", 2, "BOR", "test2");
    assertValues(iterator.next(), "D", "path/to/c.file", "C", "func3", 3, "COR", "test3");
  }
}
