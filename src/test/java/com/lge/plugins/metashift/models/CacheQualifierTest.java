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

package com.lge.plugins.metashift.models;

import com.lge.plugins.metashift.models.CacheQualifier;
import com.lge.plugins.metashift.models.CacheSet;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSet;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheQualifier class
 *
 * @author Sung Gon Kim
 */
public class CacheQualifierTest {
  private CacheQualifier qualifier;
  private CacheSet caches;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    caches = new CacheSet();
    qualifier = new CacheQualifier(0.5f);
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  @Test
  public void testInitialState() throws Exception {
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.0f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testEmptyCacheSet() throws Exception {
    caches.accept(qualifier);
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.0f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testPremirrorOnlyCacheWhichNotQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.PREMIRROR));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.3f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.1f);
    assertEquals(0.0f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testPremirrorOnlyCacheWhichQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.PREMIRROR));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
    assertEquals(0.5f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.1f);
    assertEquals(0.0f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testSharedStateOnlyCacheWhichNotQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.SHAREDSTATE));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.3f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.1f);
  }

  @Test
  public void testSharedStateOnlyCacheWhichQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.SHAREDSTATE));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.5f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.1f);
  }

  @Test
  public void testMixedCacheWhichNotQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.SHAREDSTATE));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
  }

  @Test
  public void testMixedCacheWhichQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", true, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.SHAREDSTATE));
    caches.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(qualifier);
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(CacheData.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.0f, qualifier.collection(CacheData.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testRecipeWithMixedCacheWhichNotQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", false, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.SHAREDSTATE));
    recipe = new Recipe("A-B-C");
    recipe.set(caches);
    recipe.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
  }

  @Test
  public void testRecipeWithMixedCacheWhichQualified() throws Exception {
    caches.add(new CacheData("A", "do_A", true, CacheData.Type.PREMIRROR));
    caches.add(new CacheData("A", "do_B", true, CacheData.Type.SHAREDSTATE));
    caches.add(new CacheData("A", "do_C", false, CacheData.Type.SHAREDSTATE));
    recipe = new Recipe("A-B-C");
    recipe.set(caches);
    recipe.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(qualifier);
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
  }

  @Test
  public void testRecipeSetWithCompoundCacheSetWhichQualified() throws Exception {
    caches = new CacheSet();
    caches.add(new CacheData("A", "do_fetch", true, CacheData.Type.SHAREDSTATE));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(caches);
    recipes.add(recipe);

    caches = new CacheSet();
    caches.add(new CacheData("B", "do_fetch", true, CacheData.Type.SHAREDSTATE));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(caches);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
  }
}
