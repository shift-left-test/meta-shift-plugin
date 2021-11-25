/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the StatementCoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class StatementCoverageEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setStatementCoverageThreshold(50);
    evaluator = new StatementCoverageEvaluator(configuration);
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
  public void testParseRecipesWithNoTestData() {
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, true));
    assertValues(2, 2, 1.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    assertValues(2, 0, 0.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    assertValues(2, 1, 0.5);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    recipe2.add(new PassedTestData("B-B-B", "B", "X", "X"));
    recipe2.add(new StatementCoverageData("B-B-B", "a.file", 1, false));
    recipe2.add(new StatementCoverageData("B-B-B", "b.file", 1, false));
    assertValues(4, 0, 0.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    recipe2.add(new PassedTestData("B-B-B", "B", "X", "X"));
    recipe2.add(new StatementCoverageData("B-B-B", "a.file", 1, true));
    recipe2.add(new StatementCoverageData("B-B-B", "b.file", 1, true));
    assertValues(4, 2, 0.5);
    assertStatus(true, true);
  }
}
