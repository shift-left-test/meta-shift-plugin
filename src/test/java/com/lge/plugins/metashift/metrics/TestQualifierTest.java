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
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TestQualifier class.
 *
 * @author Sung Gon Kim
 */
public class TestQualifierTest {

  private TestQualifier qualifier;
  private TestList list;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new TestQualifier(0.5f);
    list = new TestList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int denominator, int numerator, boolean available, boolean qualified,
      float passed, float failed, float error, float skipped) {
    assertEquals(denominator, qualifier.getDenominator());
    assertEquals(numerator, qualifier.getNumerator());
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(passed, qualifier.get(PassedTestCounter.class).getRatio(), 0.1f);
    assertEquals(failed, qualifier.get(FailedTestCounter.class).getRatio(), 0.1f);
    assertEquals(error, qualifier.get(ErrorTestCounter.class).getRatio(), 0.1f);
    assertEquals(skipped, qualifier.get(SkippedTestCounter.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, false, false, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testEmptySet() {
    list.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() {
    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new SkippedTestData("A", "d.suite", "c.tc", "msg"));
    list.accept(qualifier);
    assertValues(4, 1, true, false, 0.25f, 0.25f, 0.25f, 0.25f);
  }

  @Test
  public void testSetWithQualified() {

    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    list.accept(qualifier);
    assertValues(2, 1, true, true, 0.5f, 0.5f, 0.0f, 0.0f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() {
    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    list.add(new ErrorTestData("A", "a.suite", "c.tc", "msg"));
    list.add(new SkippedTestData("A", "d.suite", "c.tc", "msg"));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(4, 1, true, false, 0.25f, 0.25f, 0.25f, 0.25f);
  }

  @Test
  public void testRecipeWithQualified() {
    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(2, 1, true, true, 0.5f, 0.5f, 0.0f, 0.0f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeListWithoutQualified() {
    list = new TestList();
    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new TestList();
    list.add(new ErrorTestData("B", "a.suite", "c.tc", "msg"));
    list.add(new SkippedTestData("B", "d.suite", "c.tc", "msg"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(4, 1, true, false, 0.25f, 0.25f, 0.25f, 0.25f);
  }

  @Test
  public void testRecipeListWithQualified() {
    list = new TestList();
    list.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new TestList();
    list.add(new PassedTestData("B", "a.suite", "a.tc", "msg"));
    list.add(new FailedTestData("B", "a.suite", "b.tc", "msg"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(4, 2, true, true, 0.5f, 0.5f, 0.0f, 0.0f);
  }
}