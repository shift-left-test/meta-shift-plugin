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

import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.CommentList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CommentQualifier class.
 *
 * @author Sung Gon Kim
 */
public class CommentQualifierTest {

  private CommentQualifier qualifier;
  private CommentList list;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new CommentQualifier(0.5f);
    list = new CommentList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int denominator, int numerator, boolean available, boolean qualified,
      float ratio) {
    assertEquals(denominator, qualifier.getDenominator());
    assertEquals(numerator, qualifier.getNumerator());
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(ratio, qualifier.get(CommentCounter.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testEmptySet() {
    list.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() {
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 0));
    list.accept(qualifier);
    assertValues(20, 5, true, false, 0.25f);
  }

  @Test
  public void testSetWithQualified() {
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 5));
    list.accept(qualifier);
    assertValues(20, 10, true, true, 0.5f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() {
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 0));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(20, 5, true, false, 0.25f);
  }

  @Test
  public void testRecipeWithQualified() {
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 5));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(20, 10, true, true, 0.5f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testRecipeListWithoutQualified() {
    list = new CommentList();
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 0));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new CommentList();
    list.add(new CommentData("B", "a.file", 10, 5));
    list.add(new CommentData("B", "b.file", 10, 0));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(40, 10, true, false, 0.25f);
  }

  @Test
  public void testRecipeListWithQualified() {
    list = new CommentList();
    list.add(new CommentData("A", "a.file", 10, 5));
    list.add(new CommentData("A", "b.file", 10, 5));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new CommentList();
    list.add(new CommentData("B", "a.file", 10, 5));
    list.add(new CommentData("B", "b.file", 10, 5));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(40, 20, true, true, 0.5f);
  }
}