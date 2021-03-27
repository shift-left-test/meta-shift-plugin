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

package com.lge.plugins.metashift.metrics;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CacheList;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CacheQualifier class.
 *
 * @author Sung Gon Kim
 */
public class CacheQualifierTest {

  private CacheQualifier qualifier;
  private CacheList set;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new CacheQualifier(0.5f);
    set = new CacheList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(boolean available, boolean qualified, float premirror,
      float sharedState) {
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(premirror, qualifier.get(PremirrorCacheCounter.class).getRatio(), 0.1f);
    assertEquals(sharedState, qualifier.get(SharedStateCacheCounter.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testEmptyCacheList() {
    set.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testPremirrorOnlyCacheWhichNotQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new PremirrorCacheData("A", "do_B", false));
    set.add(new PremirrorCacheData("A", "do_C", false));
    set.accept(qualifier);
    assertValues(true, false, 0.3f, 0.0f);
  }

  @Test
  public void testPremirrorOnlyCacheWhichQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new PremirrorCacheData("A", "do_B", false));
    set.accept(qualifier);
    assertValues(true, true, 0.5f, 0.0f);
  }

  @Test
  public void testSharedStateOnlyCacheWhichNotQualified() {
    set.add(new SharedStateCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", false));
    set.add(new SharedStateCacheData("A", "do_C", false));
    set.accept(qualifier);
    assertValues(true, false, 0.0f, 0.3f);
  }

  @Test
  public void testSharedStateOnlyCacheWhichQualified() {
    set.add(new SharedStateCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", false));
    set.accept(qualifier);
    assertValues(true, true, 0.0f, 0.5f);
  }

  @Test
  public void testMixedCacheWhichNotQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", false));
    set.add(new SharedStateCacheData("A", "do_C", false));
    set.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f);
  }

  @Test
  public void testMixedCacheWhichQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", true));
    set.add(new SharedStateCacheData("A", "do_C", false));
    set.accept(qualifier);
    assertValues(true, true, 1.0f, 0.5f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithMixedCacheWhichNotQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", false));
    set.add(new SharedStateCacheData("A", "do_C", false));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, false, 1.0f, 0.0f);
  }

  @Test
  public void testRecipeWithMixedCacheWhichQualified() {
    set.add(new PremirrorCacheData("A", "do_A", true));
    set.add(new SharedStateCacheData("A", "do_B", true));
    set.add(new SharedStateCacheData("A", "do_C", false));
    recipe = new Recipe("A-B-C");
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, true, 1.0f, 0.5f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeListWithCompoundCacheListWhichQualified() {
    set = new CacheList();
    set.add(new PremirrorCacheData("A", "do_packagedata", true));
    set.add(new SharedStateCacheData("A", "do_fetch", true));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new CacheList();
    set.add(new PremirrorCacheData("B", "do_packagedata", true));
    set.add(new SharedStateCacheData("B", "do_fetch", true));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, true, 1.0f, 1.0f);
  }
}
