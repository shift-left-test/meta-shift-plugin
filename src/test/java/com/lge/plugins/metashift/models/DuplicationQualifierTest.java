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
 * Unit tests for the DuplicationQualifier class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationQualifierTest {
  private DuplicationQualifier qualifier;
  private DuplicationSet set;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    qualifier = new DuplicationQualifier(0.5f);
    set = new DuplicationSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  private void assertValues(boolean available, boolean qualified, float ratio) {
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(ratio, qualifier.collection().getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testEmptySet() throws Exception {
    set.accept(qualifier);
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() throws Exception {
    set.add(new DuplicationData("A", "a.file", 10, 6));
    set.accept(qualifier);
    assertValues(true, false, 0.6f);
  }

  @Test
  public void testSetWithQualifed() throws Exception {
    set.add(new DuplicationData("A", "b.file", 10, 4));
    set.accept(qualifier);
    assertValues(true, true, 0.5f);
  }

  @Test
  public void testSetWithCompoundData() throws Exception {
    set.add(new DuplicationData("A", "a.file", 10, 6));
    set.add(new DuplicationData("A", "b.file", 10, 4));
    set.accept(qualifier);
    assertValues(true, true, 0.5f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(qualifier);
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() throws Exception {
    set.add(new DuplicationData("A", "a.file", 10, 6));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, false, 0.6f);
  }

  @Test
  public void testRecipeWithQualified() throws Exception {
    set.add(new DuplicationData("A", "b.file", 10, 4));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, true, 0.5f);
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(qualifier);
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testRecipeSetWithoutQualified() throws Exception {
    set = new DuplicationSet();
    set.add(new DuplicationData("A", "a.file", 10, 10));
    set.add(new DuplicationData("A", "b.file", 10, 5));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new DuplicationSet();
    set.add(new DuplicationData("B", "a.file", 10, 10));
    set.add(new DuplicationData("B", "b.file", 10, 5));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, false, 0.75f);
  }

  @Test
  public void testRecipeSetWithQualified() throws Exception {
    set = new DuplicationSet();
    set.add(new DuplicationData("A", "a.file", 10, 5));
    set.add(new DuplicationData("A", "b.file", 10, 0));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new DuplicationSet();
    set.add(new DuplicationData("B", "a.file", 10, 5));
    set.add(new DuplicationData("B", "b.file", 10, 0));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, true, 0.25f);
  }
}
