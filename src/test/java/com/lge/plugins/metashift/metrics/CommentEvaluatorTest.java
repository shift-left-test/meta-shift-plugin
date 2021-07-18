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

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CommentEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CommentEvaluatorTest {

  private Configuration configuration;
  private CommentEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setCommentThreshold(50);
    evaluator = new CommentEvaluator(configuration);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new CodeSizeData("A-1.0.0-r0", "b.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "b.file", 10, 0));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 20, 5, 0.25);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new CodeSizeData("A-1.0.0-r0", "b.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "b.file", 10, 5));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 20, 10, 0.5);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    assertEquals(10, evaluator.parse(recipe).getDenominator());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getDenominator());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithUnqualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B-1.0.0-r0", "b.file", 10, 1, 1));
    recipe.add(new CommentData("B-1.0.0-r0", "b.file", 10, 0));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 20, 5, 0.25);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B-1.0.0-r0", "b.file", 10, 1, 1));
    recipe.add(new CommentData("B-1.0.0-r0", "b.file", 20, 15));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 30, 20, 0.66);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    assertEquals(10, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }

  @Test
  public void testUnstableBuildStatusWithNoAvailableData() {
    configuration.setCommentsAsUnstable(true);
    evaluator = new CommentEvaluator(configuration);
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testUnstableBuildStatusWithQualifiedData() {
    configuration.setCommentsAsUnstable(true);
    evaluator = new CommentEvaluator(configuration);
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testUnstableBuildStatusWithUnqualifiedData() {
    configuration.setCommentsAsUnstable(true);
    evaluator = new CommentEvaluator(configuration);
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 0));
    evaluator.parse(recipe);
    assertFalse(evaluator.isStable());
  }

  @Test
  public void testStableBuildStatusWithUnqualifiedData() {
    configuration.setCommentsAsUnstable(false);
    evaluator = new CommentEvaluator(configuration);
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 0));
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testToJsonObject() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new CodeSizeData("A-1.0.0-r0", "b.file", 10, 1, 1));
    recipe.add(new CommentData("A-1.0.0-r0", "b.file", 10, 5));
    evaluator.parse(recipe);

    JSONObject object = evaluator.toJsonObject();
    assertEquals(20, object.getLong("denominator"));
    assertEquals(10, object.getLong("numerator"));
    assertEquals(0.5, object.getDouble("ratio"), 0.1);
    assertEquals(0.5, object.getDouble("threshold"), 0.1);
    assertTrue(object.getBoolean("available"));
    assertTrue(object.getBoolean("qualified"));
  }
}
