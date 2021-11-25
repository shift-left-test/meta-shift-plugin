/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CommentEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class CommentEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setCommentThreshold(50);
    evaluator = new CommentEvaluator(configuration);
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
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 3, 1, 1));
    assertValues(0, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseRecipesWithNoCodeSizeData() {
    recipe1.add(new CommentData("A-A-A", "a.file", 3, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 3, 1));
    assertValues(6, 2, 0.33);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingleRecipeWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 3, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 3, 1, 1));
    recipe1.add(new CommentData("A-A-A", "a.file", 3, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 3, 1));
    assertValues(6, 2, 0.33);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 2, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 2, 1, 1));
    recipe1.add(new CommentData("A-A-A", "a.file", 2, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 2, 1));
    assertValues(4, 2, 0.5);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 3, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 3, 1, 1));
    recipe1.add(new CommentData("A-A-A", "a.file", 3, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 3, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 3, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 3, 1, 1));
    recipe2.add(new CommentData("B-B-B", "a.file", 3, 1));
    recipe2.add(new CommentData("B-B-B", "b.file", 3, 1));
    assertValues(12, 4, 0.33);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 2, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 2, 1, 1));
    recipe1.add(new CommentData("A-A-A", "a.file", 2, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 2, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 2, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 2, 1, 1));
    recipe2.add(new CommentData("B-B-B", "a.file", 2, 1));
    recipe2.add(new CommentData("B-B-B", "b.file", 2, 1));
    assertValues(8, 4, 0.5);
    assertStatus(true, true);
  }
}
