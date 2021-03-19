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
 * Unit tests for the RecipeViolationCollector class
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationCollectorTest {
  private RecipeViolationCollector collector;
  private RecipeViolationSet violations;
  private Recipe recipe;
  private RecipeSet recipes;

  @Before
  public void setUp() throws Exception {
    collector = new RecipeViolationCollector(MajorRecipeViolationData.class);
    violations = new RecipeViolationSet();
    recipe = new Recipe("A-B-C");
    recipes = new RecipeSet();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testEmptyViolationSet() throws Exception {
    violations.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testSetWithNonMatchingDataType() throws Exception {
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    violations.accept(collector);
    assertEquals(2, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testSetWithMatchingDataType() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "rule", "info", "error"));
    violations.accept(collector);
    assertEquals(1, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(1.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testCompoundDataSet() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    violations.accept(collector);
    assertEquals(3, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(0.33f, collector.getRatio(), 0.01f);
  }

  @Test
  public void testMultipleSets() throws Exception {
    List<RecipeViolationSet> group = new ArrayList<>();
    RecipeViolationSet violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    group.add(violations);
    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("B", "b.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    group.add(violations);

    group.forEach(o -> o.accept(collector));
    assertEquals(6, collector.getDenominator());
    assertEquals(2, collector.getNumerator());
    assertEquals(0.33f, collector.getRatio(), 0.01f);
  }

  @Test
  public void testEmptyRecipe() throws Exception {
    recipe.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipeWithNonMatchingDataType() throws Exception {
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe.set(violations);
    recipe.accept(collector);
    assertEquals(2, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipeWithMatchingDataType() throws Exception {
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe.set(violations);
    recipe.accept(collector);
    assertEquals(3, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(0.33f, collector.getRatio(), 0.01f);
  }

  @Test
  public void testEmptyRecipeSet() throws Exception {
    recipes.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testRecipeSetWithCompoundData() throws Exception {
    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe = new Recipe("A-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    violations = new RecipeViolationSet();
    violations.add(new MajorRecipeViolationData("B", "b.file", 1, "major", "major", "major"));
    violations.add(new MinorRecipeViolationData("B", "b.file", 1, "minor", "minor", "minor"));
    violations.add(new InfoRecipeViolationData("B", "b.file", 1, "info", "info", "info"));
    recipe = new Recipe("B-1.0.0-r0");
    recipe.set(violations);
    recipes.add(recipe);

    recipes.accept(collector);
    assertEquals(6, collector.getDenominator());
    assertEquals(2, collector.getNumerator());
    assertEquals(0.33f, collector.getRatio(), 0.01f);
  }
}
