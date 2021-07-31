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

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
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
  public void testParseSingRecipe() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new CommentData("A-A-A", "a.file", 10, 1));
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    assertValues(3, 1, 0.33);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new CommentData("A-A-A", "a.file", 10, 1));
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new CommentData("B-B-B", "a.file", 10, 10));
    recipe2.add(new PremirrorCacheData("B-B-B", "B", true));
    recipe2.add(new FailedTestData("B-B-B", "B", "B", "B"));
    assertValues(3, 3, 1.0);
  }
}
