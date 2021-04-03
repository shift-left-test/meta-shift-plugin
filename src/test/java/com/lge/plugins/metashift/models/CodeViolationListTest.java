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
 * Unit tests for the CodeViolationList class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private CodeViolationList objects;

  @Before
  public void setUp() {
    objects = new CodeViolationList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(CodeViolationData object, String recipe, String file, int line,
      int column, String rule, String message, String description, String severity, String tool) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(line, object.getLine());
    assertEquals(column, object.getColumn());
    assertEquals(rule, object.getRule());
    assertEquals(message, object.getMessage());
    assertEquals(description, object.getDescription());
    assertEquals(severity, object.getSeverity());
    assertEquals(tool, object.getTool());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    CodeViolationData first = new MajorCodeViolationData("A", "a.file", 1, 2, "rule", "msg", "desc",
        "E", "tool");
    CodeViolationData second = new MajorCodeViolationData("B", "b.file", 1, 2, "rule", "msg",
        "desc", "E", "tool");
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateWithUnknownPath() {
    objects = CodeViolationList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"violations\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"line\": 1,",
        "    \"column\": 100,",
        "    \"rule\": \"syntax\",",
        "  }",
        "] }"
    );
    File file = createTempFile("report/A/checkcode/sage_report.json", data);
    objects = CodeViolationList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    List<String> data = Collections.singletonList("{ \"violations\": [ ] }");
    File file = createTempFile("report/B/checkcode/sage_report.json", data);
    objects = CodeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"violations\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"line\": 1,",
        "    \"column\": 100,",
        "    \"rule\": \"NPE\",",
        "    \"message\": \"NPE_message\",",
        "    \"description\": \"NPE_desc\",",
        "    \"severity\": \"error\",",
        "    \"level\": \"major\",",
        "    \"tool\": \"cppcheck\"",
        "  }",
        "] }"
    );
    File file = createTempFile("report/C/checkcode/sage_report.json", data);

    objects = CodeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    CodeViolationData object = objects.iterator().next();
    assertValues(object, "C", "a.file", 1, 100, "NPE", "NPE_message", "NPE_desc", "error",
        "cppcheck");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    List<String> data = Arrays.asList(
        "{ \"violations\": [",
        "  {",
        "    \"file\": \"a.file\",",
        "    \"line\": 1,",
        "    \"column\": 100,",
        "    \"rule\": \"NPE\",",
        "    \"message\": \"NPE_message\",",
        "    \"description\": \"NPE_desc\",",
        "    \"severity\": \"error\",",
        "    \"level\": \"major\",",
        "    \"tool\": \"cppcheck\"",
        "  },",
        "  {",
        "    \"file\": \"b.file\",",
        "    \"line\": 2,",
        "    \"column\": 200,",
        "    \"rule\": \"cast\",",
        "    \"message\": \"cast_message\",",
        "    \"description\": \"cast_desc\",",
        "    \"severity\": \"warning\",",
        "    \"level\": \"minor\",",
        "    \"tool\": \"cpplint\"",
        "  },",
        "  {",
        "    \"file\": \"c.file\",",
        "    \"line\": 3,",
        "    \"column\": 300,",
        "    \"rule\": \"typo\",",
        "    \"message\": \"typo_message\",",
        "    \"description\": \"typo_desc\",",
        "    \"severity\": \"note\",",
        "    \"level\": \"info\",",
        "    \"tool\": \"clang-tidy\"",
        "  }",
        "] }"
    );
    File file = createTempFile("report/D/checkcode/sage_report.json", data);

    objects = CodeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(3, objects.size());

    Iterator<CodeViolationData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "a.file", 1, 100, "NPE", "NPE_message", "NPE_desc", "error",
        "cppcheck");
    assertValues(iterator.next(), "D", "b.file", 2, 200, "cast", "cast_message", "cast_desc",
        "warning", "cpplint");
    assertValues(iterator.next(), "D", "c.file", 3, 300, "typo", "typo_message", "typo_desc",
        "note", "clang-tidy");
  }
}
