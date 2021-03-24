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

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the MutationTestQualifier class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestQualifierTest {
  private MutationTestQualifier qualifier;
  private MutationTestSet set;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    qualifier = new MutationTestQualifier(0.5f);
    set = new MutationTestSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  private void assertValues(boolean available, boolean qualified, float killed, float survived) {
    assertEquals(available, qualifier.isAvailable());
    assertEquals(qualified, qualifier.isQualified());
    assertEquals(killed, qualifier.collection(KilledMutationTestData.class).getRatio(), 0.1f);
    assertEquals(survived, qualifier.collection(SurvivedMutationTestData.class).getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testEmptySet() throws Exception {
    set.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testSetWithoutQualified() throws Exception {
    set.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    set.accept(qualifier);
    assertValues(true, false, 0.33f, 0.66f);
  }

  @Test
  public void testSetWithQualified() throws Exception {
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    set.accept(qualifier);
    assertValues(true, true, 0.5f, 0.5f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeWithoutQualified() throws Exception {
    set.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, false, 0.33f, 0.66f);
  }

  @Test
  public void testRecipeWithQualified() throws Exception {
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.set(set);
    recipe.accept(qualifier);
    assertValues(true, true, 0.5f, 0.5f);
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(qualifier);
    assertValues(false, false, 0.0f, 0.0f);
  }

  @Test
  public void testRecipeSetWithoutQualified() throws Exception {
    set = new MutationTestSet();
    set.add(new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new MutationTestSet();
    set.add(new SurvivedMutationTestData("B", "a.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new SurvivedMutationTestData("B", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("B", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, false, 0.33f, 0.66f);
  }

  @Test
  public void testRecipeSetWithQualified() throws Exception {
    set = new MutationTestSet();
    set.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new MutationTestSet();
    set.add(new SurvivedMutationTestData("B", "b.file", "C", "f()", 1, "AOR", "TC"));
    set.add(new KilledMutationTestData("B", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(qualifier);
    assertValues(true, true, 0.5f, 0.5f);
  }
}