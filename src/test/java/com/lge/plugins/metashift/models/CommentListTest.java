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
 * Unit tests for the CommentList class.
 *
 * @author Sung Gon Kim
 */
public class CommentListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private CommentList objects;

  @Before
  public void setUp() {
    objects = new CommentList();
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
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    CommentData first = new CommentData("A", "a.file", 10, 5);
    CommentData second = new CommentData("B", "b.file", 10, 5);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateSetWithUnknownPath() {
    objects = CommentList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File file = folder.newFolder("report/A/checkcode");
    objects = CommentList.create(file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    List<String> data = Collections.singletonList("{ {");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = CommentList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSetWithInsufficientData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"size\": [ ",
        "  {",
        "    \"file\": \"a.file\",",
        "  }");
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = CommentList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateSetWithEmptyData() throws Exception {
    List<String> data = Collections.singletonList("{ \"size\": [] }");
    File file = createTempFile("report/B/checkcode/sage_report.json", data);
    objects = CommentList.create(file.getParentFile().getParentFile());
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
    File file = createTempFile("report/C/checkcode/sage_report.json", data);

    objects = CommentList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    CommentData object = objects.iterator().next();
    assertValues(object, "C", "a.file", 20, 5);
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
        "  \"comment_lines\": 3,",
        "} ] }");
    File file = createTempFile("report/D/checkcode/sage_report.json", data);

    objects = CommentList.create(file.getParentFile().getParentFile());
    assertEquals(2, objects.size());

    Iterator<CommentData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "a.file", 10, 5);
    assertValues(iterator.next(), "D", "b.file", 20, 3);
  }
}
