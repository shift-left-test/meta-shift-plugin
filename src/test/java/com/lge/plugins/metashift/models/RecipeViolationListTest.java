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
 * Unit tests for the RecipeViolationList class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private RecipeViolationList objects;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder, '\'', '"');
    builder = new StringBuilder();
    objects = new RecipeViolationList();
  }

  private void assertValues(RecipeViolationData object, String recipe, String file, int line,
      String rule, String severity, String description) {
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
    objects = RecipeViolationList.create(utils.getPath("unknown"));
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithNoFile() throws Exception {
    File directory = utils.createDirectory("report", "A", "checkrecipe").getParentFile();
    objects = RecipeViolationList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    RecipeViolationList.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder.append("{ 'issues': [ { 'file': 'a.bb', 'line': 1, 'severity': 'info' } ] }");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    RecipeViolationList.create(directory);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownSeverityType() throws Exception {
    File directory = utils.createDirectory("report", "A");
    builder
        .append("{")
        .append("  'issues': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'rule': 'checksum',")
        .append("      'severity': '???',")
        .append("      'description': 'checksum error'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    RecipeViolationList.create(directory);
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    File directory = utils.createDirectory("report", "B");
    builder.append("{ 'issues': [ ] }");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    objects = RecipeViolationList.create(directory);
    assertEquals(0, objects.size());
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    File directory = utils.createDirectory("report", "C");
    builder
        .append("{")
        .append("  'issues': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'rule': 'checksum',")
        .append("      'severity': 'error',")
        .append("      'description': 'checksum error'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    objects = RecipeViolationList.create(directory);
    assertEquals(1, objects.size());

    Iterator<RecipeViolationData> iterator = objects.iterator();
    assertValues(iterator.next(), "C", "a.file", 1, "checksum", "error", "checksum error");
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    File directory = utils.createDirectory("report", "D");
    builder
        .append("{")
        .append("  'issues': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'line': 1,")
        .append("      'rule': 'checksum',")
        .append("      'severity': 'error',")
        .append("      'description': 'checksum error'")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'line': 2,")
        .append("      'rule': 'indent',")
        .append("      'severity': 'warning',")
        .append("      'description': 'indent warning'")
        .append("    },")
        .append("    {")
        .append("      'file': 'c.file',")
        .append("      'line': 3,")
        .append("      'rule': 'typo',")
        .append("      'severity': 'info',")
        .append("      'description': 'typo info'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    objects = RecipeViolationList.create(directory);
    assertEquals(3, objects.size());

    Iterator<RecipeViolationData> iterator = objects.iterator();
    assertValues(iterator.next(), "D", "a.file", 1, "checksum", "error", "checksum error");
    assertValues(iterator.next(), "D", "b.file", 2, "indent", "warning", "indent warning");
    assertValues(iterator.next(), "D", "c.file", 3, "typo", "info", "typo info");
  }
}
