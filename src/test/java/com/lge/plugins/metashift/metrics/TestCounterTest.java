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

import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.TestList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TestCounter class.
 *
 * @author Sung Gon Kim
 */
public class TestCounterTest {

  private TestCounter counter;
  private TestList list;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    counter = new PassedTestCounter();
    list = new TestList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int denominator, int numerator, float ratio) {
    assertEquals(denominator, counter.getDenominator());
    assertEquals(numerator, counter.getNumerator());
    assertEquals(ratio, counter.getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testEmptySet() {
    list.accept(counter);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testSetWithoutMatched() {
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    list.accept(counter);
    assertValues(3, 0, 0.0f);
  }

  @Test
  public void testSetWithMatched() {
    list.add(new PassedTestData("A", "a.suite", "d.tc", "msg"));
    list.accept(counter);
    assertValues(1, 1, 1.0f);
  }

  @Test
  public void testSetWithCompoundData() {
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("A", "a.suite", "d.tc", "msg"));
    list.accept(counter);
    assertValues(4, 1, 0.25f);
  }

  @Test
  public void testMultipleSets() {
    List<TestList> group = new ArrayList<>();

    list = new TestList();
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("A", "a.suite", "d.tc", "msg"));
    group.add(list);

    list = new TestList();
    list.add(new FailedTestData("B", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("B", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("B", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("B", "a.suite", "d.tc", "msg"));
    group.add(list);

    group.forEach(o -> o.accept(counter));
    assertValues(8, 2, 0.25f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(counter);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeWithoutMatched() {
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    recipe.set(list);
    recipe.accept(counter);
    assertValues(3, 0, 0.0f);
  }

  @Test
  public void testRecipeWithMatched() {
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("A", "a.suite", "d.tc", "msg"));
    recipe.set(list);
    recipe.accept(counter);
    assertValues(4, 1, 0.25f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(counter);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeListWithCompoundData() {
    list = new TestList();
    list.add(new FailedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("A", "a.suite", "d.tc", "msg"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new TestList();
    list.add(new FailedTestData("B", "a.suite", "a.tc", "msg"));
    list.add(new ErrorTestData("B", "a.suite", "b.tc", "msg"));
    list.add(new SkippedTestData("B", "a.suite", "c.tc", "msg"));
    list.add(new PassedTestData("B", "a.suite", "d.tc", "msg"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(counter);
    assertValues(8, 2, 0.25f);
  }
}
