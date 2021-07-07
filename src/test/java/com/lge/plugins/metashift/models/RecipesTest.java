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
import static org.junit.Assert.assertFalse;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Unit tests for the Recipes class
 *
 * @author Sung Gon Kim
 */
public class RecipesTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
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

  @Test
  public void testAddMultipleObjects() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "X", true));
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipes.add(recipe);

    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "Y", false));
    recipe.add(new FailedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));
    recipes.add(recipe);

    recipe = new Recipe("C-1.0.0-r0");
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", "X", true));
    recipe.add(new SkippedTestData("C-1.0.0-r0", "c.suite", "c.tc", "msg"));
    recipes.add(recipe);

    assertEquals(3, recipes.objects(CacheData.class).count());
    assertEquals(2, recipes.objects(SharedStateCacheData.class).count());
    assertEquals(3, recipes.objects(TestData.class).count());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownPath()
      throws IOException, InterruptedException {
    new Recipes(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithoutDirectory()
      throws IOException, InterruptedException {
    new Recipes(new FilePath(utils.createFile("path-to-file")));
  }

  @Test
  public void testCreateWithEmptyReportDirectory()
      throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    recipes = new Recipes(new FilePath(report));
    assertEquals(0, recipes.size());
  }

  @Test
  public void testCreateWithoutSubDirectories()
      throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createFile(report, "a.file");
    recipes = new Recipes(new FilePath(report));
    assertEquals(0, recipes.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInvalidDirectories()
      throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createDirectory(report, "invalid");
    new Recipes(new FilePath(report));
  }

  @Test
  public void testCreateWithMultipleDirectories()
      throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createDirectory(report, "cmake-project-1.0.0-r0");
    utils.createDirectory(report, "qmake5-project-1.0.0-r0");
    utils.createDirectory(report, "autotools-project-1.0.0-r0");
    recipes = new Recipes(new FilePath(report));
    assertEquals(3, recipes.size());
    assertEquals(0, recipes.objects(Data.class).count());
    assertFalse(recipes.isAvailable(Data.class));
  }

  @Test
  public void testRecipeLogs()
      throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createDirectory(report, "cmake-project-1.0.0-r0");
    utils.createDirectory(report, "qmake5-project-1.0.0-r0");
    utils.createDirectory(report, "autotools-project-1.0.0-r0");
    PrintStream logger = Mockito.mock(PrintStream.class);
    recipes = new Recipes(new FilePath(report), logger);
    Mockito.verify(logger).println("[Recipes] cmake-project-1.0.0-r0: processing");
    Mockito.verify(logger).println("[Recipes] qmake5-project-1.0.0-r0: processing");
    Mockito.verify(logger).println("[Recipes] autotools-project-1.0.0-r0: processing");
  }
}
