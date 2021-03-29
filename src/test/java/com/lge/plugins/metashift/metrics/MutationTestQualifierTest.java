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

import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MutationTestList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MutationTestQualifier class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestQualifierTest {

  private MutationTestQualifier qualifier;
  private MutationTestList list;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    qualifier = new MutationTestQualifier(0.5f);
    list = new MutationTestList();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
  }

  private void assertValues(int denominator, int numerator, boolean available, boolean qualified, float killed, float survived) {
    assertEquals(denominator, qualifier.getDenominator());
    assertEquals(numerator, qualifier.getNumerator());
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(killed, qualifier.get(KilledMutationTestCounter.class).getRatio(), 0.1f);
    assertEquals(survived, qualifier.get(SurvivedMutationTestCounter.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, false, false, 0.0f, 0.0f);
  }

  @Test
  public void testEmptySet() {
    list.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() {
    list.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    list.accept(qualifier);
    assertValues(3, 1, true, false, 0.33f, 0.66f);
  }

  @Test
  public void testSetWithQualified() {
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    list.accept(qualifier);
    assertValues(2, 1, true, true, 0.5f, 0.5f);
  }

  @Test
  public void testEmptyRecipe() {
    recipe.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() {
    list.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(3, 1, true, false, 0.33f, 0.66f);
  }

  @Test
  public void testRecipeWithQualified() {
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.set(list);
    recipe.accept(qualifier);
    assertValues(2, 1, true, true, 0.5f, 0.5f);
  }

  @Test
  public void testEmptyRecipeList() {
    recipes.accept(qualifier);
    assertValues(0, 0, false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeListWithoutQualified() {
    list = new MutationTestList();
    list.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new MutationTestList();
    list.add(new SurvivedMutationTestData("B", "a.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new SurvivedMutationTestData("B", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("B", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(6, 2, true, false, 0.33f, 0.66f);
  }

  @Test
  public void testRecipeListWithQualified() {
    list = new MutationTestList();
    list.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    list = new MutationTestList();
    list.add(new SurvivedMutationTestData("B", "b.file", "C", "f()", 1, "AOR", "TC"));
    list.add(new KilledMutationTestData("B", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(list);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(4, 2, true, true, 0.5f, 0.5f);
  }
}
