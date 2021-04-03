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

package com.lge.plugins.metashift.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SizeData;
import com.lge.plugins.metashift.models.SizeList;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the SizeQualifier class.
 *
 * @author Sung Gon Kim
 */
public class SizeQualifierTest {

  private SizeQualifier qualifier;
  private SizeList list;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new SizeQualifier();
    list = new SizeList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int recipes, int files, int lines, int functions, int classes) {
    assertEquals(0, qualifier.getDenominator());
    assertEquals(0, qualifier.getNumerator());
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(recipes, qualifier.getRecipes());
    assertEquals(files, qualifier.getFiles());
    assertEquals(lines, qualifier.getLines());
    assertEquals(functions, qualifier.getFunctions());
    assertEquals(classes, qualifier.getClasses());
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testEmptyList() {
    list.accept(qualifier);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testRecipeWithData() {
    list.add(new SizeData("A", "a.file", 3, 2, 1));
    list.add(new SizeData("A", "b.file", 6, 4, 2));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(1, 2, 9, 6, 3);
  }

  @Test
  public void testRecipeListWithCompooundData() {
    recipe = new Recipe("A-1.0.0-r0");
    list = new SizeList();
    list.add(new SizeData("A", "a.file", 3, 2, 1));
    recipe.set(list);
    recipes.add(recipe);

    recipe = new Recipe("B-1.0.0-r0");
    list = new SizeList();
    list.add(new SizeData("B", "b.file", 6, 4, 2));
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(2, 2, 9, 6, 3);
  }
}