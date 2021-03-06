/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the RecipeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setRecipeViolationThreshold(0.5);
    evaluator = new RecipeViolationEvaluator(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    evaluation = new NegativeEvaluation(false, 0, 0, 0.0);
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
  public void testParseRecipesWithNoRecipeSizeData() {
    recipe1.add(new MajorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new MinorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new InfoRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    assertValues(0, 3, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new RecipeSizeData("A-A-A", "a.file", 1));
    recipe1.add(new MajorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new MinorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new InfoRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    assertValues(1, 3, 3.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new RecipeSizeData("A-A-A", "a.file", 10));
    recipe1.add(new MajorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new MinorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new InfoRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    assertValues(10, 3, 0.3);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new RecipeSizeData("A-A-A", "a.file", 1));
    recipe1.add(new MajorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new MinorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new InfoRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe2.add(new RecipeSizeData("B-B-B", "a.file", 1));
    recipe2.add(new MajorRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    recipe2.add(new MinorRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    recipe2.add(new InfoRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    assertValues(2, 6, 3.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new RecipeSizeData("A-A-A", "a.file", 10));
    recipe1.add(new MajorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new MinorRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe1.add(new InfoRecipeViolationData("A-A-A", "a.file", 1, "X", "X", "X"));
    recipe2.add(new RecipeSizeData("B-B-B", "a.file", 10));
    recipe2.add(new MajorRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    recipe2.add(new MinorRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    recipe2.add(new InfoRecipeViolationData("B-B-B", "a.file", 1, "X", "X", "X"));
    assertValues(20, 6, 0.3);
    assertStatus(true, true);
  }
}
