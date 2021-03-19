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

import com.lge.plugins.metashift.models.Recipe;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Recipe class.
 *
 * @author Sung Gon Kim
 */
public class RecipeTest {
  private Recipe origin;
  private Recipe same;

  @Before
  public void setUp() throws Exception {
    origin = new Recipe("cmake-project-1.0.0-r0");
    same = new Recipe("cmake-project-1.0.0-r0");
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals("cmake-project", origin.getRecipe());
    assertEquals("1.0.0", origin.getVersion());
    assertEquals("r0", origin.getRevision());
  }

  @Test(expected=IllegalArgumentException.class)
  public void testInitWithEmptyString() throws Exception {
    new Recipe("");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testInitWithInsufficientFullname() throws Exception {
    new Recipe("cmake-project");
  }

  @Test
  public void testInitWithComplexFullname() throws Exception {
    Recipe recipe = new Recipe("qtbase+-native-5.15.2+gitAUTOINC+40143c189b-r+1.0");
    assertEquals("qtbase+-native", recipe.getRecipe());
    assertEquals("5.15.2+gitAUTOINC+40143c189b", recipe.getVersion());
    assertEquals("r+1.0", recipe.getRevision());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new Recipe("qmake5-project-1.0.0-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.1.1-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.0.0-r1"));
  }

  @Test
  public void testHashCode() throws Exception {
    Recipe different = new Recipe("qmake5-project-1.0.0-r0");
    assertEquals(origin.hashCode(), origin.hashCode());
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), different.hashCode());
  }

  @Test
  public void testComparable() throws Exception {
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
  public void testDuplicates() throws Exception {
    Set<Recipe> recipes = new HashSet<>();
    recipes.add(origin);
    recipes.add(same);
    assertEquals(1, recipes.size());
  }
}
