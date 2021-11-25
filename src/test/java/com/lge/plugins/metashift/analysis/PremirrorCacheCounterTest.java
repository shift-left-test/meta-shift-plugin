/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the PremirrorCacheCounter class.
 *
 * @author Sung Gon Kim
 */
public class PremirrorCacheCounterTest {

  private Counter counter;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    counter = new PremirrorCacheCounter();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    distribution = new Distribution(0, 0);
  }

  private void assertValues(long first, long second) {
    distribution = counter.parse(recipes);
    assertEquals(first, distribution.getFirst().getCount());
    assertEquals(second, distribution.getSecond().getCount());
  }

  private void assertRatios(double first, double second) {
    distribution = counter.parse(recipes);
    assertEquals(first, distribution.getFirst().getRatio(), 0.01);
    assertEquals(second, distribution.getSecond().getRatio(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0);
    assertRatios(0.0, 0.0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new SharedStateCacheData("A-A-A", "A", true));
    assertValues(0, 0);
    assertRatios(0.0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", true));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    assertValues(1, 1);
    assertRatios(0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", true));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    recipe2.add(new PremirrorCacheData("B-B-B", "C", true));
    recipe2.add(new PremirrorCacheData("B-B-B", "D", false));
    assertValues(2, 2);
    assertRatios(0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipesWithDuplicates() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", true));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", false));
    recipe2.add(new PremirrorCacheData("B-B-B", "A", true));
    recipe2.add(new PremirrorCacheData("B-B-B", "B", false));
    assertValues(1, 1);
    assertRatios(0.5, 0.5);
  }
}
