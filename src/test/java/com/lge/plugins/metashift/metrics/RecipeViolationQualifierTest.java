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

import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.RecipeViolationList;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the RecipeViolationQualifier class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationQualifierTest {

  private RecipeViolationQualifier qualifier;
  private RecipeViolationList set;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new RecipeViolationQualifier(0.5f);
    set = new RecipeViolationList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(boolean available, boolean qualified, float major, float minor,
      float info) {
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(major, qualifier.get(MajorRecipeViolationCounter.class).getRatio(), 0.1f);
    assertEquals(minor, qualifier.get(MinorRecipeViolationCounter.class).getRatio(), 0.1f);
    assertEquals(info, qualifier.get(InfoRecipeViolationCounter.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testEmptySet() {
    set.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() {
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    set.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f, 0.0f);
  }

  @Test
  public void testSetWithQualified() {
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    set.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    set.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    set.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() {
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithQualified() {
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    set.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    set.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeListWithoutQualified() {
    set = new RecipeViolationList();
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    set.add(new MajorRecipeViolationData("A", "a.file", 2, "major", "major", "major"));
    set.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new RecipeViolationList();
    set.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    set.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, false, 0.6f, 0.2f, 0.2f);
  }

  @Test
  public void testRecipeListWithQualified() {
    set = new RecipeViolationList();
    set.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    set.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    set.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new RecipeViolationList();
    set.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    set.add(new MinorRecipeViolationData("B", "b.file", 1, "minor", "minor", "minor"));
    set.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }
}
