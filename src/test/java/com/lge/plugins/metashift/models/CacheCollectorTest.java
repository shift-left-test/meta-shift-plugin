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

import com.lge.plugins.metashift.models.CacheCollector;
import com.lge.plugins.metashift.models.Caches;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheCollector class
 *
 * @author Sung Gon Kim
 */
public class CacheCollectorTest {
  private CacheCollector collector;
  private Caches caches;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() throws Exception {
    collector = new CacheCollector(Caches.Type.SHAREDSTATE);
    caches = new Caches();
    recipe = new Recipe("A-B-C");
    recipes = new Recipes();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testEmptyCache() throws Exception {
    caches.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testNonMatchingTypeCache() throws Exception {
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    caches.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testMatchingTypeCache() throws Exception {
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    caches.accept(collector);
    assertEquals(1, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(1.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testCompoundCaches() throws Exception {
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("A", "do_compile", false, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    caches.add(new Caches.Data("A", "do_compile", false, Caches.Type.PREMIRROR));
    caches.accept(collector);
    assertEquals(2, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(0.5f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testMultipleCaches() throws Exception {
    List<Caches> group = new ArrayList<>();
    Caches caches = new Caches();
    caches.add(new Caches.Data("A", "do_test", true, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("A", "do_fetch", false, Caches.Type.SHAREDSTATE));
    group.add(caches);
    caches = new Caches();
    caches.add(new Caches.Data("B", "do_test", true, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("B", "do_fetch", false, Caches.Type.SHAREDSTATE));
    group.add(caches);

    group.forEach(o -> o.accept(collector));
    assertEquals(4, collector.getDenominator());
    assertEquals(2, collector.getNumerator());
    assertEquals(0.5f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipeWithNonMatchingCache() throws Exception {
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    recipe = new Recipe("A-B-C");
    recipe.set(caches);
    recipe.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipeWithMatchingCache() throws Exception {
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    recipe = new Recipe("A-B-C");
    recipe.set(caches);
    recipe.accept(collector);
    assertEquals(1, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(1.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testEmptyRecipes() throws Exception {
    recipes.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipesWithCompoundCaches() throws Exception {
    caches = new Caches();
    caches.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(caches);
    recipes.add(recipe);

    caches = new Caches();
    caches.add(new Caches.Data("B", "do_fetch", true, Caches.Type.SHAREDSTATE));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(caches);
    recipes.add(recipe);

    recipes.accept(collector);
    assertEquals(2, collector.getDenominator());
    assertEquals(2, collector.getNumerator());
    assertEquals(1.0f, collector.getRatio(), 0.0f);
  }
}
