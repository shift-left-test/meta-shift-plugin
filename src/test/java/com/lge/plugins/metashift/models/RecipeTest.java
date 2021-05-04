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
import static org.junit.Assert.assertNotEquals;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Unit tests for the Recipe class.
 *
 * @author Sung Gon Kim
 */
public class RecipeTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private Recipe origin;
  private Recipe same;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    origin = new Recipe("cmake-project-1.0.0-r0");
    same = new Recipe("cmake-project-1.0.0-r0");
  }

  @Test
  public void testInitialState() {
    assertEquals("cmake-project-1.0.0-r0", origin.getRecipe());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInitWithEmptyString() {
    new Recipe("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInitWithInsufficientName() {
    new Recipe("cmake-project");
  }

  @Test
  public void testInitWithComplexName() {
    String name = "A.B.C.qtbase+-native-5.15.2+gitAUTOINC+40143c189b-X-r+1.0-X";
    Recipe recipe = new Recipe(name);
    assertEquals(name, recipe.getRecipe());
  }

  @Test
  public void testEquality() {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertNotEquals(origin, new Recipe("qmake5-project-1.0.0-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.1.1-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.0.0-r1"));
  }

  @Test
  public void testHashCode() {
    Recipe different = new Recipe("qmake5-project-1.0.0-r0");
    assertEquals(origin.hashCode(), origin.hashCode());
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), different.hashCode());
  }

  @Test
  public void testComparable() {
    List<Recipe> expected = new ArrayList<>();
    expected.add(new Recipe("A-2.0.0-r3"));
    expected.add(new Recipe("A-3.0.0-r2"));
    expected.add(new Recipe("B-1.0.0-r1"));

    List<Recipe> actual = new ArrayList<>();
    actual.add(new Recipe("B-1.0.0-r1"));
    actual.add(new Recipe("A-3.0.0-r2"));
    actual.add(new Recipe("A-2.0.0-r3"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void testDuplicates() {
    Set<Recipe> recipes = new HashSet<>();
    recipes.add(origin);
    recipes.add(same);
    assertEquals(1, recipes.size());
  }

  @Test
  public void testAddMultipleObjects() {
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "X", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "Y", false));
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", "X", true));
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));
    recipe.add(new SkippedTestData("C-1.0.0-r0", "c.suite", "c.tc", "msg"));
    assertEquals(3, recipe.objects(CacheData.class).count());
    assertEquals(2, recipe.objects(SharedStateCacheData.class).count());
    assertEquals(3, recipe.objects(TestData.class).count());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeWithUnknownPath() {
    new Recipe(utils.getPath("path-to-unknown"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeWithoutDirectory() throws IOException {
    new Recipe(utils.createFile("path-to-file"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecipeWithMalformedDirectoryName() throws IOException {
    new Recipe(utils.createDirectory("report", "ABC"));
  }

  @Test
  public void testCreateRecipeWithEmptyRecipeDirectory() throws IOException {
    File directory = utils.createDirectory("report", "cmake-project-1.0.0-r0");
    Recipe recipe = new Recipe(directory);
    assertEquals("cmake-project-1.0.0-r0", recipe.getRecipe());
    assertEquals(0, recipe.objects(Data.class).count());
    assertFalse(recipe.isAvailable(Data.class));
  }

  @Test
  public void testLogging() throws IOException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    PrintStream logger = Mockito.mock(PrintStream.class);
    new Recipe(directory, logger);
    Mockito.verify(logger, Mockito.times(10))
        .printf(Mockito.startsWith("[Recipe] B-1.0.0-r0: processing"), Mockito.anyString());
  }
}
