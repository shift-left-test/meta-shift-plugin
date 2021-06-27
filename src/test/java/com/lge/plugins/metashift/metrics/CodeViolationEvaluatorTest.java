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
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CodeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationEvaluatorTest {

  private CodeViolationEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;
  private Configuration configuration;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setCodeViolationThreshold(0.5);
    evaluator = new CodeViolationEvaluator(configuration);
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
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_package", true));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getMajor(), 0, 0, 0.0);
    assertCounter(evaluator.getMinor(), 0, 0, 0.0);
    assertCounter(evaluator.getInfo(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoCodeSizeData() {
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 1, 0.0);
    assertCounter(evaluator.getMajor(), 1, 1, 1.0);
    assertCounter(evaluator.getMinor(), 1, 0, 0.0);
    assertCounter(evaluator.getInfo(), 1, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 3, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("A-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 2, 0.6);
    assertCounter(evaluator.getMajor(), 2, 1, 0.5);
    assertCounter(evaluator.getMinor(), 2, 1, 0.5);
    assertCounter(evaluator.getInfo(), 2, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("A-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new InfoCodeViolationData("A-1.0.0-r0", "c.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 10, 3, 0.3);
    assertCounter(evaluator.getMajor(), 3, 1, 0.3);
    assertCounter(evaluator.getMinor(), 3, 1, 0.3);
    assertCounter(evaluator.getInfo(), 3, 1, 0.3);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 2, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    assertEquals(2, evaluator.parse(recipe).getDenominator());
    assertEquals(1, evaluator.parse(recipe).getNumerator());
    recipe = new Recipe("A-1.0.0-r0");
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getDenominator());
    assertEquals(0, evaluator.parse(new Recipe("A-1.0.0-r0")).getNumerator());
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
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 2, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B-1.0.0-r0", "a.file", 3, 1, 1));
    recipe.add(
        new MajorCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("B-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 5, 3, 0.6);
    assertCounter(evaluator.getMajor(), 3, 2, 0.6);
    assertCounter(evaluator.getMinor(), 3, 1, 0.3);
    assertCounter(evaluator.getInfo(), 3, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 5, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new InfoCodeViolationData("B-1.0.0-r0", "c.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B-1.0.0-r0", "a.file", 5, 1, 1));
    recipe.add(
        new MajorCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("B-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 10, 4, 0.4);
    assertCounter(evaluator.getMajor(), 4, 2, 0.5);
    assertCounter(evaluator.getMinor(), 4, 1, 0.25);
    assertCounter(evaluator.getInfo(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 2, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    assertEquals(2, evaluator.parse(recipes).getDenominator());
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
    configuration.setCodeViolationsAsUnstable(false);
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
    configuration.setCodeViolationsAsUnstable(false);
    assertTrue(evaluator.isStable(configuration));
  }

  @Test
  public void testToJsonObject() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("A-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new InfoCodeViolationData("A-1.0.0-r0", "c.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    JSONObject object = evaluator.toJsonObject();
    assertEquals(3, object.getJSONObject("major").getLong("denominator"));
    assertEquals(1, object.getJSONObject("major").getLong("numerator"));
    assertEquals(3, object.getJSONObject("minor").getLong("denominator"));
    assertEquals(1, object.getJSONObject("minor").getLong("numerator"));
    assertEquals(3, object.getJSONObject("info").getLong("denominator"));
    assertEquals(1, object.getJSONObject("info").getLong("numerator"));
    assertEquals(10, object.getLong("denominator"));
    assertEquals(3, object.getLong("numerator"));
    assertTrue(object.getBoolean("available"));
    assertTrue(object.getBoolean("qualified"));
  }
}
