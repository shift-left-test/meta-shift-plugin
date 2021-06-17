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

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CoverageEvaluatorTest {

  private CoverageEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Configuration();
    criteria.setCoverageThreshold(50);
    evaluator = new CoverageEvaluator(criteria);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getStatement(), 0, 0, 0.0);
    assertCounter(evaluator.getBranch(), 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getStatement(), 0, 0, 0.0);
    assertCounter(evaluator.getBranch(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getStatement(), 0, 0, 0.0);
    assertCounter(evaluator.getBranch(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", 1, 1, false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
    assertCounter(evaluator.getStatement(), 2, 1, 0.5);
    assertCounter(evaluator.getBranch(), 1, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", 1, 1, true));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 3, 2, 0.6);
    assertCounter(evaluator.getStatement(), 2, 1, 0.5);
    assertCounter(evaluator.getBranch(), 1, 1, 1.0);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    assertEquals(1, evaluator.parse(recipe).getDenominator());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getDenominator());
  }

  @Test
  public void testParseEmptyRecipes() {
    evaluator.parse(recipes);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getStatement(), 0, 0, 0.0);
    assertCounter(evaluator.getBranch(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithUnqualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "b.file", 1, false));
    recipe.add(new BranchCoverageData("B-1.0.0-r0", "c.file", 1, 1, false));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
    assertCounter(evaluator.getStatement(), 2, 1, 0.5);
    assertCounter(evaluator.getBranch(), 1, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "b.file", 1, false));
    recipe.add(new BranchCoverageData("B-1.0.0-r0", "c.file", 1, 1, true));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 3, 2, 0.6);
    assertCounter(evaluator.getStatement(), 2, 1, 0.5);
    assertCounter(evaluator.getBranch(), 1, 1, 1.0);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    assertEquals(1, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }
}
