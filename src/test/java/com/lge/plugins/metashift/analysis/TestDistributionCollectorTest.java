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
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedTestData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TestDistributionCollector class.
 *
 * @author Sung Gon Kim
 */
public class TestDistributionCollectorTest {

  private DistributionCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    collector = new TestDistributionCollector();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    distribution = new Distribution(0, 0, 0, 0);
  }

  private void assertValues(long first, long second, long third, long fourth) {
    distribution = collector.parse(recipes);
    assertEquals(first, distribution.getFirst().getCount());
    assertEquals(second, distribution.getSecond().getCount());
    assertEquals(third, distribution.getThird().getCount());
    assertEquals(fourth, distribution.getFourth().getCount());
  }

  private void assertRatios(double first, double second, double third, double fourth) {
    distribution = collector.parse(recipes);
    assertEquals(first, distribution.getFirst().getRatio(), 0.01);
    assertEquals(second, distribution.getSecond().getRatio(), 0.01);
    assertEquals(third, distribution.getThird().getRatio(), 0.01);
    assertEquals(fourth, distribution.getFourth().getRatio(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0, 0);
    assertRatios(0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(0, 0, 0, 0);
    assertRatios(0.0, 0.0, 0.0, 0.0);
  }

  @Test
  public void testParseSingRecipe() {
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "X"));
    recipe1.add(new FailedTestData("A-A-A", "A", "B", "X"));
    recipe1.add(new ErrorTestData("A-A-A", "A", "C", "X"));
    recipe1.add(new SkippedTestData("A-A-A", "A", "D", "X"));
    assertValues(1, 1, 1, 1);
    assertRatios(0.25, 0.25, 0.25, 0.25);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "X"));
    recipe1.add(new FailedTestData("A-A-A", "A", "B", "X"));
    recipe1.add(new ErrorTestData("A-A-A", "A", "C", "X"));
    recipe1.add(new SkippedTestData("A-A-A", "A", "D", "X"));
    recipe2.add(new PassedTestData("B-B-B", "A", "A", "X"));
    recipe2.add(new FailedTestData("B-B-B", "A", "B", "X"));
    recipe2.add(new ErrorTestData("B-B-B", "A", "C", "X"));
    recipe2.add(new SkippedTestData("B-B-B", "A", "D", "X"));
    assertValues(2, 2, 2, 2);
    assertRatios(0.25, 0.25, 0.25, 0.25);
  }
}
