/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CodeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setCodeViolationThreshold(0.5);
    evaluator = new CodeViolationEvaluator(configuration);
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
    assertValues(1, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseRecipesWithNoCodeSizeData() {
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(0, 3, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(1, 3, 3.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(10, 3, 0.3);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 1, 1, 1));
    recipe2.add(new MajorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new MinorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new InfoCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(2, 6, 3.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new MajorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new MinorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new InfoCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(20, 6, 0.3);
    assertStatus(true, true);
  }
}
