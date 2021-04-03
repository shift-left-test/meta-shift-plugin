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
 * Unit tests for the ComplexityList class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private ComplexityList objects;

  @Before
  public void setUp() {
    objects = new ComplexityList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(ComplexityData object, String recipe, String file, String function,
      int start, int end, int value) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(function, object.getFunction());
    assertEquals(start, object.getStart());
    assertEquals(end, object.getEnd());
    assertEquals(value, object.getValue());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    ComplexityData first = new ComplexityData("A", "a.file", "f()", 5, 10, 1);
    ComplexityData second = new ComplexityData("B", "b.file", "g()", 5, 10, 1);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = ComplexityList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File file = folder.newFolder("report/A/checkcode");
    objects = ComplexityList.create(file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    List<String> data = Collections.singletonList("{ {");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = ComplexityList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"complexity\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"value\": 1",
        "  }",
        "] }"
    );
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = ComplexityList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    List<String> data = Collections.singletonList("{ \"complexity\": [ ] }");
    File file = createTempFile("report/B/checkcode/sage_report.json", data);
    objects = ComplexityList.create(file.getParentFile().getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"complexity\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"start\": 5,",
        "    \"end\": 10,",
        "    \"value\": 1",
        "  }",
        "] }"
    );
    File file = createTempFile("report/C/checkcode/sage_report.json", data);

    objects = ComplexityList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    ComplexityData object = objects.iterator().next();
    assertValues(object, "C", "a.file", "func1()", 5, 10, 1);
  }

  @Test
  public void testCreateWithDuplicatedData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"complexity\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"start\": 5,",
        "    \"end\": 10,",
        "    \"value\": 1",
        "  },",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"start\": 15,",
        "    \"end\": 20,",
        "    \"value\": 3",
        "  },",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"start\": 25,",
        "    \"end\": 30,",
        "    \"value\": 7",
        "  }",
        "] }"
    );
    File file = createTempFile("report/D/checkcode/sage_report.json", data);

    objects = ComplexityList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    ComplexityData object = objects.iterator().next();
    assertValues(object, "D", "a.file", "func1()", 25, 30, 7);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"complexity\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"function\": \"func1()\",",
        "    \"start\": 5,",
        "    \"end\": 10,",
        "    \"value\": 1",
        "  },",
        "  {",
        "    \"file\": \"b.file\",",
        "    \"function\": \"func2()\",",
        "    \"start\": 15,",
        "    \"end\": 20,",
        "    \"value\": 3",
        "  },",
        "  {",
        "    \"file\": \"c.file\",",
        "    \"function\": \"func3()\",",
        "    \"start\": 25,",
        "    \"end\": 30,",
        "    \"value\": 7",
        "  }",
        "] }"
    );
    File file = createTempFile("report/E/checkcode/sage_report.json", data);

    objects = ComplexityList.create(file.getParentFile().getParentFile());
    assertEquals(3, objects.size());

    Iterator<ComplexityData> iterator = objects.iterator();
    assertValues(iterator.next(), "E", "a.file", "func1()", 5, 10, 1);
    assertValues(iterator.next(), "E", "b.file", "func2()", 15, 20, 3);
    assertValues(iterator.next(), "E", "c.file", "func3()", 25, 30, 7);
  }
}
