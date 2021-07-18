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
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MutationTestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestEvaluatorTest {

  private Configuration configuration;
  private MutationTestEvaluator evaluator;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setMutationTestThreshold(50);
    evaluator = new MutationTestEvaluator(configuration);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getKilled(), 0, 0, 0.0);
    assertCounter(evaluator.getSurvived(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipe() {
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getKilled(), 0, 0, 0.0);
    assertCounter(evaluator.getSurvived(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithNoMatchingData() {
    recipe.add(new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, false, false);
    assertCounter(evaluator, 0, 0, 0.0);
    assertCounter(evaluator.getKilled(), 0, 0, 0.0);
    assertCounter(evaluator.getSurvived(), 0, 0, 0.0);
    assertCounter(evaluator.getSkipped(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithUnqualifiedData() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
    assertCounter(evaluator.getKilled(), 3, 1, 0.3);
    assertCounter(evaluator.getSurvived(), 3, 1, 0.3);
    assertCounter(evaluator.getSkipped(), 3, 1, 0.3);
  }

  @Test
  public void testParseRecipeWithQualifiedData() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "d.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getKilled(), 4, 2, 0.5);
    assertCounter(evaluator.getSurvived(), 4, 1, 0.25);
    assertCounter(evaluator.getSkipped(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipeResetValues() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
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
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new KilledMutationTestData("B-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, false);
    assertCounter(evaluator, 3, 1, 0.3);
    assertCounter(evaluator.getKilled(), 3, 1, 0.3);
    assertCounter(evaluator.getSurvived(), 3, 1, 0.3);
    assertCounter(evaluator.getSkipped(), 3, 1, 0.3);
  }

  @Test
  public void testParseRecipesWithQualifiedData() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipes.add(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new KilledMutationTestData("B-1.0.0-r0", "d.file", "C", "f()", 1, "AOR", "TC"));
    recipes.add(recipe);
    evaluator.parse(recipes);

    assertEvaluator(evaluator, true, true);
    assertCounter(evaluator, 4, 2, 0.5);
    assertCounter(evaluator.getKilled(), 4, 2, 0.5);
    assertCounter(evaluator.getSurvived(), 4, 1, 0.25);
    assertCounter(evaluator.getSkipped(), 4, 1, 0.25);
  }

  @Test
  public void testParseRecipesResetValues() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertEquals(1, evaluator.parse(recipes).getDenominator());
    assertEquals(0, evaluator.parse(new Recipes()).getDenominator());
  }

  @Test
  public void testUnstableBuildStatusWithNoAvailableData() {
    configuration.setMutationTestAsUnstable(true);
    evaluator = new MutationTestEvaluator(configuration);
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testUnstableBuildStatusWithQualifiedData() {
    configuration.setMutationTestAsUnstable(true);
    evaluator = new MutationTestEvaluator(configuration);
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testUnstableBuildStatusWithUnqualifiedData() {
    configuration.setMutationTestAsUnstable(true);
    evaluator = new MutationTestEvaluator(configuration);
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);
    assertFalse(evaluator.isStable());
  }

  @Test
  public void testStableBuildStatusWithUnqualifiedData() {
    configuration.setMutationTestAsUnstable(false);
    evaluator = new MutationTestEvaluator(configuration);
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);
    assertTrue(evaluator.isStable());
  }

  @Test
  public void testJsonObject() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "d.file", "C", "f()", 1, "AOR", "TC"));
    evaluator.parse(recipe);

    JSONObject object = evaluator.toJsonObject();
    assertEquals(4, object.getLong("denominator"));
    assertEquals(2, object.getLong("numerator"));
    assertEquals(0.5, object.getDouble("ratio"), 0.1);
    assertEquals(4, object.getJSONObject("killed").getLong("denominator"));
    assertEquals(2, object.getJSONObject("killed").getLong("numerator"));
    assertEquals(4, object.getJSONObject("survived").getLong("denominator"));
    assertEquals(1, object.getJSONObject("survived").getLong("numerator"));
    assertEquals(4, object.getJSONObject("skipped").getLong("denominator"));
    assertEquals(1, object.getJSONObject("skipped").getLong("numerator"));
    assertEquals(0.5, object.getDouble("threshold"), 0.1);
    assertTrue(object.getBoolean("available"));
    assertTrue(object.getBoolean("qualified"));
  }
}
