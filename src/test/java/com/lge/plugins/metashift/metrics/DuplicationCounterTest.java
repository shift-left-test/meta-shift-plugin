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

import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.DuplicationList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DuplicationCounter class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationCounterTest {

  private DuplicationCounter collector;
  private DuplicationList set;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    collector = new DuplicationCounter();
    set = new DuplicationList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int denominator, int numerator, float ratio) {
    assertEquals(denominator, collector.getDenominator());
    assertEquals(numerator, collector.getNumerator());
    assertEquals(ratio, collector.getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testEmptySet() {
    set.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testSetWithCompoundData() {
    set.add(new DuplicationData("A", "a.file", 10, 5));
    set.add(new DuplicationData("A", "b.file", 10, 5));
    set.accept(collector);
    assertValues(20, 10, 0.5f);
  }

  @Test
  public void testMultipleSets() {
    List<DuplicationList> group = new ArrayList<>();

    set = new DuplicationList();
    set.add(new DuplicationData("A", "a.file", 10, 5));
    set.add(new DuplicationData("A", "b.file", 10, 5));
    group.add(set);

    set = new DuplicationList();
    set.add(new DuplicationData("B", "a.file", 10, 5));
    set.add(new DuplicationData("B", "b.file", 10, 5));
    group.add(set);

    group.forEach(o -> o.accept(collector));
    assertValues(40, 20, 0.5f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeWithData() {
    set.add(new DuplicationData("A", "a.file", 10, 5));
    set.add(new DuplicationData("A", "b.file", 10, 5));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(20, 10, 0.5f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeListWithCompoundData() {
    set = new DuplicationList();
    set.add(new DuplicationData("A", "a.file", 10, 5));
    set.add(new DuplicationData("A", "b.file", 10, 5));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new DuplicationList();
    set.add(new DuplicationData("B", "a.file", 10, 5));
    set.add(new DuplicationData("B", "b.file", 10, 5));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(collector);
    assertValues(40, 20, 0.5f);
  }
}
