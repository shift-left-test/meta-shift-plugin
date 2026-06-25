/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the RecipeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    evaluator = new RecipeEvaluator(configuration);
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

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    // UnitTestEvaluator: available=true, qualified=true (1/1 = 100% >= 50%)
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    // StatementCoverageEvaluator: available=true, qualified=false (0/1 = 0% < 50%)
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    // denominator=2 (UnitTests available, StatementCoverage available), numerator=1 (UnitTests qualified)
    assertValues(2, 1, 0.5);
  }

  @Test
  public void testParseMultipleRecipes() {
    // recipe1: UnitTests available+qualified (1/2=50%>=50%), StatementCoverage available+qualified (1/1=100%)
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    // recipe2: UnitTests(same aggregated pool), MutationTests available+qualified (1/1=100%)
    recipe2.add(new FailedTestData("B-B-B", "B", "B", "B"));
    recipe2.add(new KilledMutationTestData("B-B-B", "b.file", "X", "X", 1, "X", "X"));
    // total: UnitTests(1/2=50%->qualified), StatementCoverage(1/1->qualified), MutationTests(1/1->qualified)
    // BranchCoverage: not available
    // denominator=3, numerator=3, ratio=1.0
    assertValues(3, 3, 1.0);
  }
}
