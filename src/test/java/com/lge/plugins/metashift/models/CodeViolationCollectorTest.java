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
 * Unit tests for the CodeViolationCollector class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationCollectorTest {
  private CodeViolationCollector collector;
  private CodeViolationSet set;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    collector = new CodeViolationCollector(MajorCodeViolationData.class);
    set = new CodeViolationSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  private void assertValues(int denominator, int numerator, float ratio) {
    assertEquals(denominator, collector.getDenominator());
    assertEquals(numerator, collector.getNumerator());
    assertEquals(ratio, collector.getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testEmptySet() throws Exception {
    set.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testSetWithoutMatched() throws Exception {
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    set.accept(collector);
    assertValues(2, 0, 0.0f);
  }

  @Test
  public void testSetWithMatched() throws Exception {
    set.add(new MajorCodeViolationData("A", "a.file", 3, 3, "r", "m", "d", "E", "t"));
    set.accept(collector);
    assertValues(1, 1, 1.0f);
  }

  @Test
  public void testSetWithCompoundData() throws Exception {
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("A", "a.file", 3, 3, "r", "m", "d", "E", "t"));
    set.accept(collector);
    assertValues(3, 1, 0.3f);
  }

  @Test
  public void testMultipleSets() throws Exception {
    List<CodeViolationSet> group = new ArrayList<>();

    set = new CodeViolationSet();
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("A", "a.file", 3, 3, "r", "m", "d", "E", "t"));
    group.add(set);

    set = new CodeViolationSet();
    set.add(new MinorCodeViolationData("B", "b.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("B", "b.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("B", "b.file", 3, 3, "r", "m", "d", "E", "t"));
    group.add(set);

    group.forEach(o -> o.accept(collector));
    assertValues(6, 2, 0.3f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeWithoutMatched() throws Exception {
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(2, 0, 0.0f);
  }

  @Test
  public void testRecipeWithMatched() throws Exception {
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("A", "a.file", 3, 3, "r", "m", "d", "E", "t"));
    recipe.set(set);
    recipe.accept(collector);
    assertValues(3, 1, 0.3f);
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(collector);
    assertValues(0, 0, 0.0f);
  }

  @Test
  public void testRecipeSetWithCompoundData() throws Exception {
    set = new CodeViolationSet();
    set.add(new MinorCodeViolationData("A", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("A", "a.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("A", "a.file", 3, 3, "r", "m", "d", "E", "t"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    set = new CodeViolationSet();
    set.add(new MinorCodeViolationData("B", "b.file", 1, 1, "r", "m", "d", "E", "t"));
    set.add(new InfoCodeViolationData("B", "b.file", 2, 2, "r", "m", "d", "E", "t"));
    set.add(new MajorCodeViolationData("B", "b.file", 3, 3, "r", "m", "d", "E", "t"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(set);
    recipes.add(recipe);

    recipes.accept(collector);
    assertValues(6, 2, 0.3f);
  }
}