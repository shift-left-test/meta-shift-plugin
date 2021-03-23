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

import java.io.*;
import java.util.*;
import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the SizeSet class.
 *
 * @author Sung Gon Kim
 */
public class SizeSetTest {
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private SizeSet objects;

  @Before
  public void setUp() throws Exception {
    objects = new SizeSet();
  }

  private File createTempFile(String dirname, String filename, String[] data) throws Exception {
    File directory = folder.newFolder(dirname);
    File file = new File(directory, filename);
    FileWriter writer = new FileWriter(file);
    for (int i = 0; i < data.length; i++) {
      writer.write(data[i]);
    }
    writer.close();
    return file;
  }

  private void assertValues(SizeData object, String recipe, String file,
                            int lines, int functions, int classes) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(lines, object.getLines());
    assertEquals(functions, object.getFunctions());
    assertEquals(classes, object.getClasses());
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() throws Exception {
    SizeData first = new SizeData("A", "a.file", 3, 2, 1);
    SizeData second = new SizeData("B", "b.file", 3, 2, 1);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.iterator().next());
  }

  @Test
  public void testAddingDuplicates() throws Exception {
    objects.add(new SizeData("A", "a.file", 3, 2, 1));
    objects.add(new SizeData("A", "a.file", 30, 20, 10));
    assertEquals(1, objects.size());
  }

  @Test
  public void testCreateSetWithUnknownPath() throws Exception {
    objects = SizeSet.create("A", new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSetWithMalformedFile() throws Exception {
    String[] data = {
      "{ \"size\": [ ",
      "  {",
      "    \"file\": \"a.file\",",
      "  }",
     };
    File file = createTempFile("report/A", "sage_report.json", data);
    objects = SizeSet.create("A", file.getParentFile());
  }

  @Test
  public void testCreateSetWithEmptyData() throws Exception {
    String[] data = { "{ \"size\": [] }" };
    File file = createTempFile("report/A", "sage_report.json", data);
    objects = SizeSet.create("A", file.getParentFile());
    assertEquals(0, objects.size());
  }


  @Test
  public void testCreateSetWithSingleData() throws Exception {
    String[] data = {
      "{ \"size\": [ ",
      "  {",
      "    \"file\": \"a.file\",",
      "    \"total_lines\": 20,",
      "    \"code_lines\": 1,",
      "    \"comment_lines\": 5,",
      "    \"duplicated_lines\": 2,",
      "    \"functions\": 15,",
      "    \"classes\": 6",
      "  } ",
      "] }"
    };
    File file = createTempFile("report/A", "sage_report.json", data);

    objects = SizeSet.create("A", file.getParentFile());
    assertEquals(1, objects.size());

    SizeData object = objects.iterator().next();
    assertValues(object, "A", "a.file", 20, 15, 6);
  }

  @Test
  public void testCreateSetWithMultipleData() throws Exception {
    String[] data = {
      "{ \"size\": [ ",
      "{",
      "  \"file\": \"a.file\",",
      "  \"total_lines\": 10,",
      "  \"functions\": 10,",
      "  \"classes\": 5",
      "},",
      "{",
      "  \"file\": \"b.file\",",
      "  \"total_lines\": 20,",
      "  \"functions\": 20,",
      "  \"classes\": 10",
      "} ] }"
    };
    File file = createTempFile("report/A", "sage_report.json", data);

    objects = SizeSet.create("A", file.getParentFile());
    assertEquals(2, objects.size());

    Iterator<SizeData> iterator = objects.iterator();
    assertValues(iterator.next(), "A", "a.file", 10, 10, 5);
    assertValues(iterator.next(), "A", "b.file", 20, 20, 10);
  }
}
