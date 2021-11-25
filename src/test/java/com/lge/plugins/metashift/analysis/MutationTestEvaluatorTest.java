/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MutationTestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setMutationTestThreshold(50);
    evaluator = new MutationTestEvaluator(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    evaluation = new PositiveEvaluation(false, 0, 0, 0.0);
  }

  private void assertValues(long denominator, long numerator, double ratio) {
    evaluation = evaluator.parse(recipes);
    assertEquals(denominator, evaluation.getDenominator());
    assertEquals(numerator, evaluation.getNumerator());
    assertEquals(ratio, evaluation.getRatio(), 0.01);
  }

  private void assertStatus(boolean available, boolean qualified) {
    evaluation = evaluator.parse(recipes);
    assertEquals(available, evaluation.isAvailable());
    assertEquals(qualified, evaluation.isQualified());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(0, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SkippedMutationTestData("A-A-A", "c.file", "X", "X", 1, "X", "X"));
    assertValues(3, 1, 0.33);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    assertValues(2, 1, 0.5);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SkippedMutationTestData("A-A-A", "c.file", "X", "X", 1, "X", "X"));
    recipe2.add(new KilledMutationTestData("B-B-B", "a.file", "X", "X", 1, "X", "X"));
    recipe2.add(new SurvivedMutationTestData("B-B-B", "b.file", "X", "X", 1, "X", "X"));
    recipe2.add(new SkippedMutationTestData("B-B-B", "c.file", "X", "X", 1, "X", "X"));
    assertValues(6, 2, 0.33);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    recipe2.add(new KilledMutationTestData("B-B-B", "a.file", "X", "X", 1, "X", "X"));
    recipe2.add(new SurvivedMutationTestData("B-B-B", "b.file", "X", "X", 1, "X", "X"));
    assertValues(4, 2, 0.5);
    assertStatus(true, true);
  }
}
