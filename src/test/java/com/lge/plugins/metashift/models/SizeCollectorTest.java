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

import com.lge.plugins.metashift.models.SizeCollector;
import com.lge.plugins.metashift.models.Sizes;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the SizeCollector class
 *
 * @author Sung Gon Kim
 */
public class SizeCollectorTest {
  private SizeCollector collector;
  private Sizes sizes;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() throws Exception {
    collector = new SizeCollector();
    sizes = new Sizes();
    recipe = new Recipe("A-B-C");
    recipes = new Recipes();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, collector.getRecipes());
    assertEquals(0, collector.getFiles());
    assertEquals(0, collector.getLines());
    assertEquals(0, collector.getFunctions());
    assertEquals(0, collector.getClasses());
  }

  @Test
  public void testEmptySizes() throws Exception {
    sizes.accept(collector);
    assertEquals(0, collector.getRecipes());
    assertEquals(0, collector.getFiles());
    assertEquals(0, collector.getLines());
    assertEquals(0, collector.getFunctions());
    assertEquals(0, collector.getClasses());
  }

  @Test
  public void testSizesWithMultipleSizesData() throws Exception {
    sizes.add(new Sizes.Data("A", "a.file", 3, 2, 1));
    sizes.add(new Sizes.Data("A", "b.file", 6, 4, 2));
    sizes.accept(collector);
    assertEquals(1, collector.getRecipes());
    assertEquals(2, collector.getFiles());
    assertEquals(9, collector.getLines());
    assertEquals(6, collector.getFunctions());
    assertEquals(3, collector.getClasses());
  }

  @Test
  public void testRecipeWithMultipleSizesData() throws Exception {
    sizes.add(new Sizes.Data("A", "a.file", 3, 2, 1));
    sizes.add(new Sizes.Data("A", "b.file", 6, 4, 2));
    recipe.set(sizes);
    recipe.accept(collector);
    assertEquals(1, collector.getRecipes());
    assertEquals(2, collector.getFiles());
    assertEquals(9, collector.getLines());
    assertEquals(6, collector.getFunctions());
    assertEquals(3, collector.getClasses());
  }

  @Test
  public void testRecipesWithMultipleRecipes() throws Exception {
    recipe = new Recipe("A-1.0.0-r0");
    sizes = new Sizes();
    sizes.add(new Sizes.Data("A", "a.file", 3, 2, 1));
    recipe.set(sizes);
    recipes.add(recipe);

    recipe = new Recipe("B-1.0.0-r0");
    sizes = new Sizes();
    sizes.add(new Sizes.Data("B", "b.file", 6, 4, 2));
    recipe.set(sizes);
    recipes.add(recipe);

    recipes.accept(collector);
    assertEquals(2, collector.getRecipes());
    assertEquals(2, collector.getFiles());
    assertEquals(9, collector.getLines());
    assertEquals(6, collector.getFunctions());
    assertEquals(3, collector.getClasses());
  }
}
