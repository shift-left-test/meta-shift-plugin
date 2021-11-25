/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Recipes class
 *
 * @author Sung Gon Kim
 */
public class RecipesTest {

  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
  }

  @Test
  public void testInitialState() {
    assertEquals(0, recipes.size());
  }

  @Test
  public void testAddingData() {
    Recipe first = new Recipe("A-1.0.0-r0");
    Recipe second = new Recipe("B-1.0.0-r0");
    recipes.add(second);
    recipes.add(first);
    assertEquals(2, recipes.size());
    assertEquals(first, recipes.get(1));
  }

  @Test
  public void testAddMultipleObjects() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "X", true));
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipes.add(recipe);

    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "Y", false));
    recipe.add(new FailedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));
    recipes.add(recipe);

    recipe = new Recipe("C-1.0.0-r0");
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", "X", true));
    recipe.add(new SkippedTestData("C-1.0.0-r0", "c.suite", "c.tc", "msg"));
    recipes.add(recipe);

    assertEquals(3, recipes.objects(CacheData.class).count());
    assertEquals(2, recipes.objects(SharedStateCacheData.class).count());
    assertEquals(3, recipes.objects(TestData.class).count());
  }
}
