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

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the SizeCounter class.
 *
 * @author Sung Gon Kim
 */
public class SizeCounterTest {
  private SizeCounter collector;
  private SizeSet set;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    collector = new SizeCounter();
    set = new SizeSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  private void assertValues(int recipes, int files, int lines, int functions, int classes) {
    assertEquals(recipes, collector.getRecipes());
    assertEquals(files, collector.getFiles());
    assertEquals(lines, collector.getLines());
    assertEquals(functions, collector.getFunctions());
    assertEquals(classes, collector.getClasses());
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testEmptySet() throws Exception {
    set.accept(collector);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testSetWithData() throws Exception {
    set.add(new SizeData("A", "a.file", 3, 2, 1));
    set.add(new SizeData("A", "b.file", 6, 4, 2));
    set.accept(collector);
    assertValues(1, 2, 9, 6, 3);
  }

  @Test
  public void testRecipeWithData() throws Exception {
    set.add(new SizeData("A", "a.file", 3, 2, 1));
    set.add(new SizeData("A", "b.file", 6, 4, 2));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(1, 2, 9, 6, 3);
  }

  @Test
  public void testRecipeSetWithCompoundData() throws Exception {
    recipe = new Recipe("A-1.0.0-r0");
    set = new SizeSet();
    set.add(new SizeData("A", "a.file", 3, 2, 1));
    recipe.set(set);
    recipes.add(recipe);

    recipe = new Recipe("B-1.0.0-r0");
    set = new SizeSet();
    set.add(new SizeData("B", "b.file", 6, 4, 2));
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(collector);
    assertValues(2, 2, 9, 6, 3);
  }
}
