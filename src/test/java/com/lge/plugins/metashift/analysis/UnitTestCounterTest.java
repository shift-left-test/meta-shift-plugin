/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
 * Unit tests for the UnitTestCounter class.
 *
 * @author Sung Gon Kim
 */
public class UnitTestCounterTest {

  private Counter counter;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    counter = new UnitTestCounter();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    distribution = new Distribution(0, 0, 0, 0);
  }

  private void assertValues(long first, long second, long third, long fourth) {
    distribution = counter.parse(recipes);
    assertEquals(first, distribution.getFirst().getCount());
    assertEquals(second, distribution.getSecond().getCount());
    assertEquals(third, distribution.getThird().getCount());
    assertEquals(fourth, distribution.getFourth().getCount());
  }

  private void assertRatios(double first, double second, double third, double fourth) {
    distribution = counter.parse(recipes);
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
  public void testParseSingleRecipe() {
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
