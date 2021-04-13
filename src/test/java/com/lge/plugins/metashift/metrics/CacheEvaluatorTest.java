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

import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the CacheEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CacheEvaluatorTest {

  private CacheEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Criteria();
    criteria.setCacheThreshold(0.5);
    evaluator = new CacheEvaluator(criteria);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPremirror(), 0, 0, 0.0);
    assertCounter(evaluator.getSharedState(), 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPremirror(), 0, 0, 0.0);
    assertCounter(evaluator.getSharedState(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new DuplicationData("A-1.0.0-r0", "a.file", 10, 5));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPremirror(), 0, 0, 0.0);
    assertCounter(evaluator.getSharedState(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedSingleTypeData() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", false));
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
    assertCounter(evaluator.getPremirror(), 3, 1, 0.3);
    assertCounter(evaluator.getSharedState(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithQualifiedSingleTypeData() {
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_package", false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 2, 1, 0.5);
    assertCounter(evaluator.getPremirror(), 0, 0, 0.0);
    assertCounter(evaluator.getSharedState(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipeWithUnqualifiedMultipleTypeData() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", false));
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", false));
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_package", false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 5, 2, 0.4);
    assertCounter(evaluator.getPremirror(), 3, 1, 0.3);
    assertCounter(evaluator.getSharedState(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipeWithQualifiedMultipleTypeData() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", false));
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_package", false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getPremirror(), 2, 1, 0.5);
    assertCounter(evaluator.getSharedState(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    assertEquals(1, evaluator.parse(recipe).getDenominator());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getDenominator());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPremirror(), 0, 0, 0.0);
    assertCounter(evaluator.getSharedState(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithUnqualifiedMultipleTypeData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", false));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", false));
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_package", false));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 5, 2, 0.4);
    assertCounter(evaluator.getPremirror(), 3, 1, 0.3);
    assertCounter(evaluator.getSharedState(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipesWithQualifiedMultipleTypeData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", false));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_package", false));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getPremirror(), 2, 1, 0.5);
    assertCounter(evaluator.getSharedState(), 2, 1, 0.5);
  }

  @Test
  public void testParseRecipesWithDuplicates() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SharedStateCacheData("X-1.0.0-r0", "do_package", true));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new SharedStateCacheData("X-1.0.0-r0", "do_package", true));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertCounter(evaluator.getSharedState(), 1, 1, 1.0);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new SharedStateCacheData("X-1.0.0-r0", "do_package", true));
    assertEquals(1, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }
}
