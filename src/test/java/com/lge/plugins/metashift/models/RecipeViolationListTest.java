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
 * Unit tests for the RecipeViolationList class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private RecipeViolationList objects;

  @Before
  public void setUp() {
    objects = new RecipeViolationList();
  }

  private File createTempFile(String path, Collection<String> lines) throws Exception {
    File directory = folder.newFolder(FilenameUtils.getPath(path));
    File file = new File(directory, FilenameUtils.getName(path));
    FileUtils.writeLines(file, lines);
    return file;
  }

  private void assertValues(
      RecipeViolationData object,
      String recipe,
      String file,
      int line,
      String rule,
      String severity,
      String description) {
    assertEquals(recipe, object.getRecipe());
    assertEquals(file, object.getFile());
    assertEquals(line, object.getLine());
    assertEquals(rule, object.getRule());
    assertEquals(severity, object.getSeverity());
    assertEquals(description, object.getDescription());
  }

  @Test
  public void testInitialState() {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() {
    RecipeViolationData first =
        new MajorRecipeViolationData("A", "a.file", 1, "rule1", "rule1_info", "error");
    RecipeViolationData second =
        new MajorRecipeViolationData("A", "a.file", 2, "rule1", "rule1_info", "error");
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.get(1));
  }

  @Test
  public void testCreateListWithUnknownPath() {
    objects = RecipeViolationList.create(new File(folder.getRoot(), "unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File file = folder.newFolder("report/A/checkrecipe");
    objects = RecipeViolationList.create(file.getParentFile());
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    List<String> data = Collections.singletonList("{ {");
    File file = createTempFile("report/A/checkrecipe/recipe_violations.json", data);
    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateListWithMalformedFile() throws Exception {
    List<String> data =
        Arrays.asList(
            "{ \"issues\": [",
            "  {",
            "    \"file\": \"a.bb\", ",
            "    \"line\": 1, ",
            "    \"severity\": \"info\" ",
            "  }",
            "] }");
    File file = createTempFile("report/A/checkrecipe/recipe_violations.json", data);
    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownSeverityType() throws Exception {
    List<String> data =
        Arrays.asList(
            "{ \"issues\": [ ",
            "  {",
            "    \"file\": \"a.file\",",
            "    \"line\": 1,",
            "    \"rule\": \"bbclassextend\",",
            "    \"severity\": \"???\",",
            "    \"description\": \"bbclassextend error\"",
            "  }",
            "] }");
    File file = createTempFile("report/A/checkrecipe/recipe_violations.json", data);
    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
  }

  @Test
  public void testCreateListWithEmptyData() throws Exception {
    List<String> data = Collections.singletonList("{ \"issues\": [] }");
    File file = createTempFile("report/B/checkrecipe/recipe_violations.json", data);
    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    List<String> data =
        Arrays.asList(
            "{ \"issues\": [ ",
            "  {",
            "    \"file\": \"a.file\",",
            "    \"line\": 1,",
            "    \"rule\": \"bbclassextend\",",
            "    \"severity\": \"error\",",
            "    \"description\": \"bbclassextend error\"",
            "  }",
            "] }");
    File file = createTempFile("report/C/checkrecipe/recipe_violations.json", data);

    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(1, objects.size());

    RecipeViolationData object = objects.iterator().next();
    assertValues(object, "C", "a.file", 1, "bbclassextend", "error", "bbclassextend error");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    List<String> data =
        Arrays.asList(
            "{ \"issues\": [ ",
            "  {",
            "    \"file\": \"a.file\",",
            "    \"line\": 1,",
            "    \"rule\": \"bbclassextend\",",
            "    \"severity\": \"error\",",
            "    \"description\": \"bbclassextend error\"",
            "  },",
            "  {",
            "    \"file\": \"b.file\",",
            "    \"line\": 2,",
            "    \"rule\": \"indent\",",
            "    \"severity\": \"warning\",",
            "    \"description\": \"indent warning\"",
            "  },",
            "  {",
            "    \"file\": \"c.file\",",
            "    \"line\": 3,",
            "    \"rule\": \"typo\",",
            "    \"severity\": \"info\",",
            "    \"description\": \"typo info\"",
            "  }",
            "] }");
    File file = createTempFile("report/D/checkrecipe/recipe_violations.json", data);

    objects = RecipeViolationList.create(file.getParentFile().getParentFile());
    assertEquals(3, objects.size());

    Iterator<RecipeViolationData> iterator = objects.iterator();
    assertValues(
        iterator.next(), "D", "a.file", 1, "bbclassextend", "error", "bbclassextend error");
    assertValues(iterator.next(), "D", "b.file", 2, "indent", "warning", "indent warning");
    assertValues(iterator.next(), "D", "c.file", 3, "typo", "info", "typo info");
  }
}
