/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DuplicationCounter class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationCounterTest {

  private Configuration configuration;
  private Counter counter;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setDuplicationTolerance(5);
    counter = new DuplicationCounter(configuration);
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
    recipe1.add(new PremirrorCacheData("A-A-A", "X", true));
    assertValues(0, 0);
    assertRatios(0.0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    assertValues(10, 10);
    assertRatios(0.5, 0.5);
  }

  @Test
  public void testParseSingleRecipeWithLowTolerance() {
    configuration.setDuplicationTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    assertValues(11, 9);
    assertRatios(0.55, 0.45);
  }

  @Test
  public void testParseSingleRecipeWithHighTolerance() {
    configuration.setDuplicationTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    assertValues(0, 20);
    assertRatios(0.0, 1.0);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 1));
    assertValues(20, 20);
    assertRatios(0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipesWithLowTolerance() {
    configuration.setDuplicationTolerance(0);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 1));
    assertValues(22, 18);
    assertRatios(0.55, 0.45);
  }

  @Test
  public void testParseMultipleRecipesWithHighTolerance() {
    configuration.setDuplicationTolerance(100);
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "a.file", 10, 1, 10));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-A-A", "b.file", 10, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "a.file", 10, 1, 10));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-B-B", "b.file", 10, 1, 1));
    assertValues(0, 40);
    assertRatios(0.0, 1.0);
  }
}
