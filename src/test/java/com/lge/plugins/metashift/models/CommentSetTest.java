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
import org.apache.commons.io.*;
import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CommentSet class.
 *
 * @author Sung Gon Kim
 */
public class CommentSetTest {
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private CommentSet objects;

  @Before
  public void setUp() throws Exception {
    objects = new CommentSet();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(CommentData object, String recipe, String file,
                            int lines, int commentLines) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(lines, object.getLines());
    assertEquals(commentLines, object.getCommentLines());
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() throws Exception {
    CommentData first = new CommentData("A", "a.file", 10, 5);
    CommentData second = new CommentData("B", "b.file", 10, 5);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.iterator().next());
  }

  @Test
  public void testAddingDuplicates() throws Exception {
    objects.add(new CommentData("A", "a.file", 10, 5));
    objects.add(new CommentData("A", "a.file", 10, 5));
    assertEquals(1, objects.size());
  }

  @Test
  public void testCreateSetWithUnknownPath() throws Exception {
    objects = CommentSet.create("A", new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSetWithMalformedFile() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"size\": [ ",
        "  {",
        "    \"file\": \"a.file\",",
        "  }");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = CommentSet.create("A", file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateSetWithEmptyData() throws Exception {
    List<String> data = Arrays.asList("{ \"size\": [] }");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = CommentSet.create("A", file.getParentFile().getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateSetWithSingleData() throws Exception {
    List<String> data = Arrays.asList(
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
        "] }");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);

    objects = CommentSet.create("A", file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    CommentData object = objects.iterator().next();
    assertValues(object, "A", "a.file", 20, 5);
  }

  @Test
  public void testCreateSetWithMultipleData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"size\": [ ",
        "{",
        "  \"file\": \"a.file\",",
        "  \"total_lines\": 10,",
        "  \"comment_lines\": 5,",
        "},",
        "{",
        "  \"file\": \"b.file\",",
        "  \"total_lines\": 20,",
        "  \"comment_lines\": 5,",
        "} ] }");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);

    objects = CommentSet.create("A", file.getParentFile().getParentFile());
    assertEquals(2, objects.size());

    Iterator<CommentData> iterator = objects.iterator();
    assertValues(iterator.next(), "A", "a.file", 10, 5);
    assertValues(iterator.next(), "A", "b.file", 20, 5);
  }
}
