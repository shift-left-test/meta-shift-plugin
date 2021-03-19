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

import com.lge.plugins.metashift.models.RecipeViolationQualifier;
import com.lge.plugins.metashift.models.RecipeViolationSet;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSet;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the RecipeViolationQualifier class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationQualifierTest {
  private RecipeViolationQualifier qualifier;
  private RecipeViolationSet violations;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    qualifier = new RecipeViolationQualifier(0.5f);
    violations = new RecipeViolationSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  private void assertValues(boolean available, boolean qualified,
                            float major, float minor, float info) {
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(major, qualifier.collection(
        MajorRecipeViolationData.class).getRatio(), 0.1f);
    assertEquals(minor, qualifier.collection(
        MinorRecipeViolationData.class).getRatio(), 0.1f);
    assertEquals(info, qualifier.collection(
        InfoRecipeViolationData.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testEmptyDataSet() throws Exception {
    violations.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testDataSetWhichNotQualified() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f, 0.0f);
  }

  @Test
  public void testDataSetWhichQualified() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    violations.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWhichNotQualified() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    recipe.set(violations);
    recipe.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWhichQualified() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe.set(violations);
    recipe.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeSetWhichNotQualified() throws Exception {
    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MajorRecipeViolationData("A", "a.file", 2, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    violations.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, false, 0.6f, 0.2f, 0.2f);
  }

  @Test
  public void testRecipeSetWhichQualified() throws Exception {
    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("B", "b.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, true, 0.3f, 0.3f, 0.3f);
  }
}
