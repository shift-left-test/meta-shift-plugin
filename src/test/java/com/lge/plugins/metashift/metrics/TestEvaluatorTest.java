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

import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedTestData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class TestEvaluatorTest {

  private TestEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Criteria();
    criteria.setTestThreshold(0.5);
    evaluator = new TestEvaluator(criteria);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPassed(), 0, 0, 0.0);
    assertCounter(evaluator.getFailed(), 0, 0, 0.0);
    assertCounter(evaluator.getError(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPassed(), 0, 0, 0.0);
    assertCounter(evaluator.getFailed(), 0, 0, 0.0);
    assertCounter(evaluator.getError(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPassed(), 0, 0, 0.0);
    assertCounter(evaluator.getFailed(), 0, 0, 0.0);
    assertCounter(evaluator.getError(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new SkippedTestData("A-1.0.0-r0", "d.suite", "c.tc", "msg"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 4, 1, 0.25);
    assertCounter(evaluator.getPassed(), 4, 1, 0.25);
    assertCounter(evaluator.getFailed(), 4, 1, 0.25);
    assertCounter(evaluator.getError(), 4, 1, 0.25);
    assertCounter(evaluator.getSkipped(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new PassedTestData("A-1.0.0-r0", "e.suite", "a.tc", "msg"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getPassed(), 4, 2, 0.5);
    assertCounter(evaluator.getFailed(), 4, 1, 0.2);
    assertCounter(evaluator.getError(), 4, 1, 0.2);
    assertCounter(evaluator.getSkipped(), 4, 0, 0.0);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertEquals(1, evaluator.parse(recipe).getDenominator());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getDenominator());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getPassed(), 0, 0, 0.0);
    assertCounter(evaluator.getFailed(), 0, 0, 0.0);
    assertCounter(evaluator.getError(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithUnqualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new FailedTestData("B-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("B-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new SkippedTestData("B-1.0.0-r0", "d.suite", "c.tc", "msg"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 4, 1, 0.25);
    assertCounter(evaluator.getPassed(), 4, 1, 0.25);
    assertCounter(evaluator.getFailed(), 4, 1, 0.25);
    assertCounter(evaluator.getError(), 4, 1, 0.25);
    assertCounter(evaluator.getSkipped(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new ErrorTestData("B-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new PassedTestData("B-1.0.0-r0", "e.suite", "a.tc", "msg"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getPassed(), 4, 2, 0.5);
    assertCounter(evaluator.getFailed(), 4, 1, 0.2);
    assertCounter(evaluator.getError(), 4, 1, 0.2);
    assertCounter(evaluator.getSkipped(), 4, 0, 0.0);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertEquals(1, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }
}
