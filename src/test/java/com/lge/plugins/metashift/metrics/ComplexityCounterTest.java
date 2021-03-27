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

import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.ComplexityList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ComplexityCounter class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityCounterTest {

  private ComplexityCounter collector;
  private ComplexityList set;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    collector = new ComplexityCounter(10);
    set = new ComplexityList();
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
  public void testSetWithoutMatched() {
    set.add(new ComplexityData("A", "a.file", "f()", 1));
    set.add(new ComplexityData("A", "a.file", "g()", 9));
    set.accept(collector);
    assertValues(2, 0, 0.0f);
  }

  @Test
  public void testSetWithMatched() {
    set.add(new ComplexityData("A", "a.file", "h()", 10));
    set.accept(collector);
    assertValues(1, 1, 1.0f);
  }

  @Test
  public void testSetWithCompoundData() {
    set.add(new ComplexityData("A", "a.file", "f()", 1));
    set.add(new ComplexityData("A", "a.file", "g()", 9));
    set.add(new ComplexityData("A", "a.file", "h()", 10));
    set.accept(collector);
    assertValues(3, 1, 0.3f);
  }

  @Test
  public void testMultipleSets() {
    List<ComplexityList> group = new ArrayList<>();

    set = new ComplexityList();
    set.add(new ComplexityData("A", "a.file", "f()", 1));
    set.add(new ComplexityData("A", "a.file", "g()", 9));
    set.add(new ComplexityData("A", "a.file", "h()", 10));
    group.add(set);

    set = new ComplexityList();
    set.add(new ComplexityData("B", "a.file", "f()", 1));
    set.add(new ComplexityData("B", "a.file", "g()", 9));
    set.add(new ComplexityData("B", "a.file", "h()", 10));
    group.add(set);

    group.forEach(o -> o.accept(collector));
    assertValues(6, 2, 0.3f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeWithoutMatched() {
    set.add(new ComplexityData("A", "a.file", "g()", 9));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(1, 0, 0.0f);
  }

  @Test
  public void testRecipeWithMatched() {
    set.add(new ComplexityData("A", "a.file", "g()", 10));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(1, 1, 1.0f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeListWithCompoundData() {
    set = new ComplexityList();
    set.add(new ComplexityData("A", "a.file", "f()", 1));
    set.add(new ComplexityData("A", "a.file", "g()", 9));
    set.add(new ComplexityData("A", "a.file", "h()", 10));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new ComplexityList();
    set.add(new ComplexityData("B", "a.file", "f()", 1));
    set.add(new ComplexityData("B", "a.file", "g()", 9));
    set.add(new ComplexityData("B", "a.file", "h()", 10));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(collector);
    assertValues(6, 2, 0.3f);
  }
}
