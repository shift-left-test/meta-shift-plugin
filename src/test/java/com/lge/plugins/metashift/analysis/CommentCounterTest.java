/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CommentCounter class.
 *
 * @author Sung Gon Kim
 */
public class CommentCounterTest {

  private Counter counter;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Distribution distribution;

  @Before
  public void setUp() {
    counter = new CommentCounter();
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
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(0, 0);
    assertRatios(0.0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CommentData("A-A-A", "a.file", 2, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 2, 1));
    assertValues(2, 2);
    assertRatios(0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CommentData("A-A-A", "a.file", 2, 1));
    recipe1.add(new CommentData("A-A-A", "b.file", 2, 1));
    recipe2.add(new CommentData("B-B-B", "a.file", 2, 1));
    recipe2.add(new CommentData("B-B-B", "b.file", 2, 1));
    assertValues(4, 4);
    assertRatios(0.5, 0.5);
  }
}
