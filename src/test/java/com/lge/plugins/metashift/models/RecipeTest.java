/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Recipe class.
 *
 * @author Sung Gon Kim
 */
public class RecipeTest {

  private Recipe origin;
  private Recipe same;

  @Before
  public void setUp() {
    origin = new Recipe("A-1.0.0-r0");
    same = new Recipe("A-1.0.0-r0");
  }

  @Test
  public void testInitialState() {
    assertEquals("A-1.0.0-r0", origin.getName());
  }

  @Test
  public void testEquality() {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertNotEquals(origin, new Recipe("qmake5-project-1.0.0-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.1.1-r0"));
    assertNotEquals(origin, new Recipe("cmake-project-1.0.0-r1"));
  }

  @Test
  public void testHashCode() {
    Recipe different = new Recipe("qmake5-project-1.0.0-r0");
    assertEquals(origin.hashCode(), origin.hashCode());
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), different.hashCode());
  }

  @Test
  public void testDuplicates() {
    Set<Recipe> recipes = new HashSet<>();
    recipes.add(origin);
    recipes.add(same);
    assertEquals(1, recipes.size());
  }

  @Test
  public void testAddMultipleObjects() {
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "X", true));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "Y", false));
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", "X", true));
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));
    recipe.add(new SkippedTestData("C-1.0.0-r0", "c.suite", "c.tc", "msg"));
    assertEquals(3, recipe.objects(CacheData.class).count());
    assertEquals(2, recipe.objects(SharedStateCacheData.class).count());
    assertEquals(3, recipe.objects(TestData.class).count());
  }
}
