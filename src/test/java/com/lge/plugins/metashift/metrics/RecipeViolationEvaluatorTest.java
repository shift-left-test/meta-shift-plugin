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

import static com.lge.plugins.metashift.metrics.TestUtils.assertCounter;
import static com.lge.plugins.metashift.metrics.TestUtils.assertEvaluator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationEvaluatorTest {

  private RecipeViolationEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;
  private Configuration configuration;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setRecipeViolationThreshold(0.5);
    evaluator = new RecipeViolationEvaluator(configuration);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getMajor(), 0, 0, 0.0);
    assertCounter(evaluator.getMinor(), 0, 0, 0.0);
    assertCounter(evaluator.getInfo(), 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getMajor(), 0, 0, 0.0);
    assertCounter(evaluator.getMinor(), 0, 0, 0.0);
    assertCounter(evaluator.getInfo(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getMajor(), 0, 0, 0.0);
    assertCounter(evaluator.getMinor(), 0, 0, 0.0);
    assertCounter(evaluator.getInfo(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoRecipeSizeData() {
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new InfoRecipeViolationData("A-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 3, 0.0);
    assertCounter(evaluator.getMajor(), 3, 1, 0.3);
    assertCounter(evaluator.getMinor(), 3, 1, 0.3);
    assertCounter(evaluator.getInfo(), 3, 1, 0.3);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "b.file", 1, "major", "major", "major"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 5, 3, 0.6);
    assertCounter(evaluator.getMajor(), 3, 2, 0.6);
    assertCounter(evaluator.getMinor(), 3, 1, 0.33);
    assertCounter(evaluator.getInfo(), 3, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new InfoRecipeViolationData("A-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 5, 2, 0.5);
    assertCounter(evaluator.getMajor(), 2, 1, 0.5);
    assertCounter(evaluator.getMinor(), 2, 0, 0.0);
    assertCounter(evaluator.getInfo(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    assertEquals(5, evaluator.parse(recipe).getDenominator());
    assertEquals(1, evaluator.parse(recipe).getNumerator());
    recipe = new Recipe("A-1.0.0-r0");
    assertEquals(0, evaluator.parse(recipe).getDenominator());
    assertEquals(0, evaluator.parse(recipe).getNumerator());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getMajor(), 0, 0, 0.0);
    assertCounter(evaluator.getMinor(), 0, 0, 0.0);
    assertCounter(evaluator.getInfo(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithUnqualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 3));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new RecipeSizeData("B-1.0.0-r0", "a.file", 2));
    recipe.add(new MajorRecipeViolationData("B-1.0.0-r0", "b.file", 1, "major", "major", "major"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 5, 3, 0.6);
    assertCounter(evaluator.getMajor(), 3, 2, 0.6);
    assertCounter(evaluator.getMinor(), 3, 1, 0.33);
    assertCounter(evaluator.getInfo(), 3, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 10));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new InfoRecipeViolationData("A-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new RecipeSizeData("B-1.0.0-r0", "a.file", 2));
    recipe.add(new MajorRecipeViolationData("B-1.0.0-r0", "b.file", 1, "major", "major", "major"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 12, 4, 0.3);
    assertCounter(evaluator.getMajor(), 4, 2, 0.5);
    assertCounter(evaluator.getMinor(), 4, 1, 0.25);
    assertCounter(evaluator.getInfo(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    assertEquals(5, evaluator.parse(recipes).getDenominator());
    assertEquals(1, evaluator.parse(recipes).getNumerator());
    recipes = new Recipes();
    assertEquals(0, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(recipes).getNumerator());
  }

  @Test
  public void testIsStableReturnFalseWithEmptyRecipe() {
    evaluator.parse(recipe);
    assertFalse(evaluator.isStable(configuration));
  }

  @Test
  public void testIsStableReturnTrueWithEmptyRecipeWhenStableSet() {
    evaluator.parse(recipe);
    configuration.setRecipeViolationsAsUnstable(false);
    assertTrue(evaluator.isStable(configuration));
  }

  @Test
  public void testIsStableReturnFalseWithEmptyRecipes() {
    evaluator.parse(recipes);
    assertFalse(evaluator.isStable(configuration));
  }

  @Test
  public void testIsStableReturnTrueWithEmptyRecipesWhenStableSet() {
    evaluator.parse(recipes);
    configuration.setRecipeViolationsAsUnstable(false);
    assertTrue(evaluator.isStable(configuration));
  }
}
