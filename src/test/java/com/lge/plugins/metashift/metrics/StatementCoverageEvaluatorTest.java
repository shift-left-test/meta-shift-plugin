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
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the StatementCoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class StatementCoverageEvaluatorTest {

  private StatementCoverageEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Configuration();
    criteria.setStatementCoverageThreshold(50);
    evaluator = new StatementCoverageEvaluator(criteria);
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
  public void testParseRecipeWithoutTestData() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 3, 1, 0.3);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, false));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, true));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 3, 2, 0.6);
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
  }

  @Test
  public void testParseRecipesWithUnqualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PassedTestData("A-1.0.0-r0", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new PassedTestData("B-1.0.0-r0", "test", "test", ""));
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "b.file", 1, false));
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "c.file", 1, false));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PassedTestData("A-1.0.0-r0", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new PassedTestData("B-1.0.0-r0", "test", "test", ""));
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "b.file", 1, false));
    recipe.add(new StatementCoverageData("B-1.0.0-r0", "c.file", 1, true));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 3, 2, 0.6);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new StatementCoverageData("A-1.0.0-r0", "a.file", 1, true));
    assertEquals(1, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }

  @Test
  public void testToJsonObject() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, true));
    evaluator.parse(recipe);

    JSONObject object = evaluator.toJsonObject();
    assertEquals(3, object.getLong("denominator"));
    assertEquals(2, object.getLong("numerator"));
    assertEquals(0.6, object.getDouble("ratio"), 0.1);
    assertEquals(0.5, object.getDouble("threshold"), 0.1);
    assertTrue(object.getBoolean("available"));
    assertTrue(object.getBoolean("qualified"));
  }
}
