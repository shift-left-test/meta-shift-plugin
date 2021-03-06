/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DuplicationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationEvaluatorTest {

  Configuration configuration;
  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setDuplicationTolerance(5);
    configuration.setDuplicationThreshold(50);
    evaluator = new DuplicationEvaluator(configuration);
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
    recipe1.add(new PremirrorCacheData("A-A-A", "X", true));
    assertValues(0, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseRecipesWithNoCodeSizeData() {
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    assertValues(0, 20, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    assertValues(30, 20, 0.66);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    assertValues(30, 10, 0.33);
    assertStatus(true, true);
  }

  @Test
  public void testParseSingleRecipeWithLowTolerance() {
    configuration.setDuplicationTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    assertValues(30, 21, 0.7);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithHighTolerance() {
    configuration.setDuplicationTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    assertValues(30, 0, 0.0);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "c.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "c.file", 10, 1, 1));
    assertValues(60, 40, 0.66);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "c.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "c.file", 10, 1, 1));
    assertValues(60, 20, 0.33);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithLowTolerance() {
    configuration.setDuplicationTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "c.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "c.file", 10, 1, 1));
    assertValues(60, 42, 0.7);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithHighTolerance() {
    configuration.setDuplicationTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "c.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "c.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "c.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "c.file", 10, 1, 1));
    assertValues(60, 0, 0.0);
    assertStatus(true, true);
  }
}
