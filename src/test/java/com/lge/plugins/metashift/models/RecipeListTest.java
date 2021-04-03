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
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the RecipeList class
 *
 * @author Sung Gon Kim
 */
public class RecipeListTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private RecipeList recipes;

  @Before
  public void setUp() {
    recipes = new RecipeList();
  }

  @Test
  public void testInitialState() {
    assertEquals(0, recipes.size());
  }

  @Test
  public void testAddingData() {
    Recipe first = new Recipe("A-1.0.0-r0");
    Recipe second = new Recipe("B-1.0.0-r0");
    recipes.add(second);
    recipes.add(first);
    assertEquals(2, recipes.size());
    assertEquals(first, recipes.get(1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeListWithUnknownPath() {
    RecipeList.create(new File(folder.getRoot(), "unknown"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeListWithoutDirectory() throws IOException {
    RecipeList.create(folder.newFile());
  }

  @Test
  public void testCreateRecipeListWithEmptyReportDirectory() throws IOException {
    File report = folder.newFolder("report");
    recipes = RecipeList.create(report);
    assertEquals(0, recipes.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeListWithInvalidDirectories() throws IOException {
    File report = folder.newFolder("report");
    FileUtils.forceMkdir(new File(report, "invalid"));
    RecipeList.create(report);
  }

  @Test
  public void testCreateRecipeListWithMultipleDirectories() throws IOException {
    File report = folder.newFolder("report");
    FileUtils.forceMkdir(new File(report, "cmake-project-1.0.0-r0"));
    FileUtils.forceMkdir(new File(report, "qmake5-project-1.0.0-r0"));
    FileUtils.forceMkdir(new File(report, "autotools-1.0.0-r0"));
    recipes = RecipeList.create(report);
    assertEquals(3, recipes.size());
  }
}
