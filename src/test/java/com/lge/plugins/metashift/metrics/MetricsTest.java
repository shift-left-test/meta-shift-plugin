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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.CacheList;
import com.lge.plugins.metashift.models.CodeViolationList;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.CommentList;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.ComplexityList;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.DuplicationList;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.MutationTestList;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.RecipeViolationList;
import com.lge.plugins.metashift.models.SizeData;
import com.lge.plugins.metashift.models.SizeList;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.models.TestList;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Metrics class.
 *
 * @author Sung Gon Kim
 */
public class MetricsTest {

  private Metrics metrics;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Criteria(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 5, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    metrics = new Metrics(criteria);
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
    recipes.add(recipe);
  }

  private void assertValues(int denominator, int numerator, boolean available, boolean qualified,
      float ratio) {
    assertEquals(denominator, metrics.getDenominator());
    assertEquals(numerator, metrics.getNumerator());
    assertEquals(available, metrics.isAvailable());
    assertEquals(qualified, metrics.isQualified());
    assertEquals(ratio, metrics.getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testWithEmptyRecipes() {
    RecipeList recipes = new RecipeList();
    recipes.accept(metrics);
    assertValues(0, 0, false, false, 0.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedCacheData() {
    recipe.get(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", false));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCacheData() {
    recipe.get(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", true));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedRecipeViolationData() {
    recipe.get(RecipeViolationList.class).add(
        new MajorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedRecipeViolationData() {
    recipe.get(RecipeViolationList.class).add(
        new MinorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithSizeData() {
    recipe.get(SizeList.class).add(new SizeData("A", "a.file", 100, 50, 10));
    recipes.accept(metrics);
    assertValues(0, 0, false, false, 0.0f);
    SizeQualifier sizeQualifier = (SizeQualifier) metrics.get(SizeQualifier.class);
    assertEquals(1, sizeQualifier.getRecipes());
    assertEquals(1, sizeQualifier.getFiles());
    assertEquals(100, sizeQualifier.getLines());
    assertEquals(50, sizeQualifier.getFunctions());
    assertEquals(10, sizeQualifier.getClasses());
  }

  @Test
  public void testRecipesWithoutQualifiedCommentData() {
    recipe.get(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 0));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCommentData() {
    recipe.get(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 5));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedCodeViolationData() {
    recipe.get(CodeViolationList.class).add(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCodeViolationData() {
    recipe.get(CodeViolationList.class).add(
        new MinorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedComplexityData() {
    recipe.get(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 5, 10, 10));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedComplexityData() {
    recipe.get(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 5, 10, 0));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedDuplicationData() {
    recipe.get(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 10));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedDuplicationData() {
    recipe.get(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 0));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedTestData() {
    recipe.get(TestList.class).add(
        new FailedTestData("A-B-C", "a.suite", "a.tc", "msg"));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedTestData() {
    recipe.get(TestList.class).add(
        new PassedTestData("A-B-C", "a.suite", "a.tc", "msg"));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedMutationTestData() {
    recipe.get(MutationTestList.class).add(
        new SurvivedMutationTestData("A-B-C", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipes.accept(metrics);
    assertValues(1, 0, true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedMutationTestData() {
    recipe.get(MutationTestList.class).add(
        new KilledMutationTestData("A-B-C", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipes.accept(metrics);
    assertValues(1, 1, true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedMixedData() {
    // Unqualified
    recipe.get(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", false));
    recipe.get(RecipeViolationList.class).add(
        new MajorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));

    // Qualified
    recipe.get(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 5));

    recipes.accept(metrics);
    assertFalse(metrics.get(CacheQualifier.class).isQualified());
    assertFalse(metrics.get(RecipeViolationQualifier.class).isQualified());
    assertTrue(metrics.get(CommentQualifier.class).isQualified());
    assertValues(3, 1, true, false, 0.3f);
  }

  @Test
  public void testRecipesWithQualifiedMixedData() {
    // Unqualified
    recipe.get(CodeViolationList.class).add(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));

    // Qualified
    recipe.get(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 5, 10, 0));
    recipe.get(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 0));

    recipes.accept(metrics);
    assertFalse(metrics.get(CodeViolationQualifier.class).isQualified());
    assertTrue(metrics.get(ComplexityQualifier.class).isQualified());
    assertTrue(metrics.get(DuplicationQualifier.class).isQualified());
    assertValues(3, 2, true, true, 0.6f);
  }
}
