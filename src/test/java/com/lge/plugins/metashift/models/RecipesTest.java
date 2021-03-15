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

import com.lge.plugins.metashift.models.Recipes;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Recipes class
 *
 * @author Sung Gon Kim
 */
public class RecipesTest {
  private Recipes recipes;

  @Before
  public void setUp() throws Exception {
    recipes = new Recipes();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, recipes.size());
  }

  @Test
  public void testAddingData() throws Exception {
    Recipe first = new Recipe("A-1.0.0-r0");
    Recipe second = new Recipe("B-1.0.0-r0");
    recipes.add(second);
    recipes.add(first);
    assertEquals(2, recipes.size());
    assertEquals(first, recipes.iterator().next());
  }

  @Test
  public void testAddingDuplicates() throws Exception {
    recipes.add(new Recipe("A-B-C"));
    recipes.add(new Recipe("A-B-C"));
    assertEquals(1, recipes.size());
  }
}
