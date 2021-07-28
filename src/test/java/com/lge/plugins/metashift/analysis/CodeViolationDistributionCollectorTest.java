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
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CodeViolationDistributionCollector class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationDistributionCollectorTest {

  private DistributionCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    collector = new CodeViolationDistributionCollector();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    distribution = new Distribution(0, 0, 0);
  }

  private void assertValues(long first, long second, long third) {
    distribution = collector.parse(recipes);
    assertEquals(first, distribution.getFirst().getCount());
    assertEquals(second, distribution.getSecond().getCount());
    assertEquals(third, distribution.getThird().getCount());
  }

  private void assertRatios(double first, double second, double third) {
    distribution = collector.parse(recipes);
    assertEquals(first, distribution.getFirst().getRatio(), 0.01);
    assertEquals(second, distribution.getSecond().getRatio(), 0.01);
    assertEquals(third, distribution.getThird().getRatio(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0);
    assertRatios(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(0, 0, 0);
    assertRatios(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseSingRecipe() {
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    assertValues(1, 1, 1);
    assertRatios(0.33, 0.33, 0.33);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new MajorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new MinorCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe1.add(new InfoCodeViolationData("A-A-A", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new MajorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new MinorCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));
    recipe2.add(new InfoCodeViolationData("B-B-B", "a.file", 1, 1, "X", "X", "X", "X", "X"));

    assertValues(2, 2, 2);
    assertRatios(0.33, 0.33, 0.33);
  }
}
