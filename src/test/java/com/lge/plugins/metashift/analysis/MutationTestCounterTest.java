/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MutationTestCounter class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestCounterTest {

  private Counter counter;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    counter = new MutationTestCounter();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    distribution = new Distribution(0, 0);
  }

  private void assertValues(long first, long second, long third) {
    distribution = counter.parse(recipes);
    assertEquals(first, distribution.getFirst().getCount());
    assertEquals(second, distribution.getSecond().getCount());
    assertEquals(third, distribution.getThird().getCount());
  }

  private void assertRatios(double first, double second, double third) {
    distribution = counter.parse(recipes);
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
  public void testParseSingleRecipe() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SkippedMutationTestData("A-A-A", "c.file", "X", "X", 1, "X", "X"));
    assertValues(1, 1, 1);
    assertRatios(0.33, 0.33, 0.33);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new KilledMutationTestData("A-A-A", "a.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SurvivedMutationTestData("A-A-A", "b.file", "X", "X", 1, "X", "X"));
    recipe1.add(new SkippedMutationTestData("A-A-A", "c.file", "X", "X", 1, "X", "X"));
    recipe2.add(new KilledMutationTestData("B-B-B", "a.file", "X", "X", 1, "X", "X"));
    recipe2.add(new SurvivedMutationTestData("B-B-B", "b.file", "X", "X", 1, "X", "X"));
    recipe2.add(new SkippedMutationTestData("B-B-B", "c.file", "X", "X", 1, "X", "X"));
    assertValues(2, 2, 2);
    assertRatios(0.33, 0.33, 0.33);
  }
}
