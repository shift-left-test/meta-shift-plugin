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

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CodeSizeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CodeSizeEvaluatorTest {

  private CodeSizeEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Criteria();
    criteria.setRecipeViolationThreshold(0.5);
    evaluator = new CodeSizeEvaluator();
    recipe = new Recipe("A-B-C");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  private void assertValues(long recipes, long files, long lines, long functions, long classes) {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertEquals(recipes, evaluator.getRecipes());
    assertEquals(files, evaluator.getFiles());
    assertEquals(lines, evaluator.getLines());
    assertEquals(functions, evaluator.getFunctions());
    assertEquals(classes, evaluator.getClasses());
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseRecipeWithData() {
    recipe.add(new CodeSizeData("A", "a.file", 3, 2, 1));
    recipe.add(new CodeSizeData("A", "b.file", 6, 4, 2));
    evaluator.parse(recipe);

    assertValues(1, 2, 9, 6, 3);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new CodeSizeData("A", "a.file", 3, 2, 1));
    assertEquals(3, evaluator.parse(recipe).getLines());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getLines());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseRecipesWithData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new CodeSizeData("A", "a.file", 3, 2, 1));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B", "b.file", 6, 4, 2));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertValues(2, 2, 9, 6, 3);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new CodeSizeData("A", "a.file", 3, 2, 1));
    assertEquals(3, evaluator.parse(recipes).getLines());
    assertEquals(0, evaluator.parse(new Recipes()).getLines());
  }
}
