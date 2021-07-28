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
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.NegativeEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ComplexityEvaluationCollector class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityEvaluationCollectorTest {

  Configuration configuration;
  private EvaluationCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setComplexityTolerance(10);
    configuration.setComplexityThreshold(50);
    collector = new ComplexityEvaluationCollector(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    evaluation = new NegativeEvaluation(false, 0, 0, 0.0);
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
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    assertValues(0, 0, 0.0);
    assertStatus(false, false);
  }

  @Test
  public void testParseRecipesWithNoCodeSizeData() {
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 20));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    assertValues(3, 2, 0.66);
    assertStatus(false, false);
  }

  @Test
  public void testParseSingRecipeWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    assertValues(3, 2, 0.66);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    assertValues(3, 1, 0.33);
    assertStatus(true, true);
  }

  @Test
  public void testParseSingRecipeWithLowTolerance() {
    configuration.setComplexityTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    assertValues(3, 3, 1.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseSingleRecipeWithHighTolerance() {
    configuration.setComplexityTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    assertValues(3, 0, 0.0);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func1", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func2", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func3", 2, 2, 1));
    assertValues(6, 4, 0.66);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithQualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func1", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func2", 1, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func3", 2, 2, 1));
    assertValues(6, 2, 0.33);
    assertStatus(true, true);
  }

  @Test
  public void testParseMultipleRecipesWithLowTolerance() {
    configuration.setComplexityTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func1", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func2", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func3", 2, 2, 1));
    assertValues(6, 6, 1.0);
    assertStatus(true, false);
  }

  @Test
  public void testParseMultipleRecipesWithHighTolerance() {
    configuration.setComplexityTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func1", 1, 1, 10));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func2", 1, 1, 1));
    recipe1.add(new ComplexityData("A-A-A", "a.file", "func3", 2, 2, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func1", 1, 1, 10));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func2", 1, 1, 1));
    recipe2.add(new ComplexityData("B-B-B", "a.file", "func3", 2, 2, 1));
    assertValues(6, 0, 0.0);
    assertStatus(true, true);
  }
}
