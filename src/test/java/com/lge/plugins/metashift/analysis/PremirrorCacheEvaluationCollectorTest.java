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
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the PremirrorCacheEvaluationCollector class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheEvaluationCollectorTest {

  private EvaluationCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setPremirrorCacheThreshold(50);
    collector = new PremirrorCacheEvaluationCollector(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    evaluation = new PositiveEvaluation(false, 0, 0, 0.0);
  }

  private void assertValues(long denominator, long numerator, double ratio) {
    evaluation = collector.parse(recipes);
    assertEquals(denominator, evaluation.getDenominator());
    assertEquals(numerator, evaluation.getNumerator());
    assertEquals(ratio, evaluation.getRatio().getValue(), 0.01);
  }

  private void assertStatus(boolean available, boolean qualified) {
    evaluation = collector.parse(recipes);
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
  public void testParseSingRecipeWithUnqualifiedData() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    assertValues(2, 0, 0.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", true));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    assertValues(2, 1, 0.5);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    recipe2.add(new PremirrorCacheData("B-B-B", "A", false));
    recipe2.add(new PremirrorCacheData("B-B-B", "B", false));
    assertValues(2, 0, 0.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", true));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    recipe2.add(new PremirrorCacheData("B-B-B", "A", true));
    recipe2.add(new PremirrorCacheData("B-B-B", "B", false));
    assertValues(2, 1, 0.5);
    assertStatus(true, true);
  }
}