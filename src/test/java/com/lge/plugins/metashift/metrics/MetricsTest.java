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

import com.lge.plugins.metashift.models.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Metrics class.
 *
 * @author Sung Gon Kim
 */
public class MetricsTest {
  private Criteria criteria;
  private Metrics metrics;
  private Recipe recipe;
  private RecipeList recipes;

  @Before
  public void setUp() throws Exception {
    criteria = new Criteria(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 5, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    metrics = new Metrics(criteria);
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
    recipes.add(recipe);
  }

  private void assertValues(boolean available, boolean qualified, float ratio) {
    assertEquals(available, metrics.isAvailable());
    assertEquals(qualified, metrics.isQualified());
    assertEquals(ratio, metrics.getRatio(), 0.1f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testWithEmptyRecipes() throws Exception {
    RecipeList recipes = new RecipeList();
    recipes.accept(metrics);
    assertValues(false, false, 0.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedCacheData() throws Exception {
    recipe.collection(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", false));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCacheData() throws Exception {
    recipe.collection(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", true));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedRecipeViolationData() throws Exception {
    recipe.collection(RecipeViolationList.class).add(
        new MajorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedRecipeViolationData() throws Exception {
    recipe.collection(RecipeViolationList.class).add(
        new MinorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedCommentData() throws Exception {
    recipe.collection(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 0));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCommentData() throws Exception {
    recipe.collection(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 5));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedCodeViolationData() throws Exception {
    recipe.collection(CodeViolationList.class).add(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedCodeViolationData() throws Exception {
    recipe.collection(CodeViolationList.class).add(
        new MinorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedComplexityData() throws Exception {
    recipe.collection(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 10));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedComplexityData() throws Exception {
    recipe.collection(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 0));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedDuplicationData() throws Exception {
    recipe.collection(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 10));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedDuplicationData() throws Exception {
    recipe.collection(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 0));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedTestData() throws Exception {
    recipe.collection(TestList.class).add(
        new FailedTestData("A-B-C", "a.suite", "a.tc", "msg"));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedTestData() throws Exception {
    recipe.collection(TestList.class).add(
        new PassedTestData("A-B-C", "a.suite", "a.tc", "msg"));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Ignore
  @Test
  public void testRecipesWithoutQualifiedCoverageData() throws Exception {
  }

  @Ignore
  @Test
  public void testRecipesWithQualifiedCoverageData() throws Exception {
  }

  @Test
  public void testRecipesWithoutQualifiedMutationTestData() throws Exception {
    recipe.collection(MutationTestList.class).add(
        new SurvivedMutationTestData("A-B-C", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipes.accept(metrics);
    assertValues(true, false, 0.0f);
  }

  @Test
  public void testRecipesWithQualifiedMutationTestData() throws Exception {
    recipe.collection(MutationTestList.class).add(
        new KilledMutationTestData("A-B-C", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipes.accept(metrics);
    assertValues(true, true, 1.0f);
  }

  @Test
  public void testRecipesWithoutQualifiedMixedData() throws Exception {
    // Unqualified
    recipe.collection(CacheList.class).add(new PremirrorCacheData("A-B-C", "X", false));
    recipe.collection(RecipeViolationList.class).add(
        new MajorRecipeViolationData("A-B-C", "a.file", 1, "e", "e", "e"));

    // Qualified
    recipe.collection(CommentList.class).add(new CommentData("A-B-C", "a.file", 10, 5));

    recipes.accept(metrics);
    assertValues(true, false, 0.3f);
  }

  @Test
  public void testRecipesWithQualifiedMixedData() throws Exception {
    // Unqualified
    recipe.collection(CodeViolationList.class).add(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 2, "r", "m", "d", "E", "t"));

    // Qualified
    recipe.collection(ComplexityList.class).add(
        new ComplexityData("A-B-C", "a.file", "f()", 0));
    recipe.collection(DuplicationList.class).add(
        new DuplicationData("A-B-C", "a.file", 10, 0));

    recipes.accept(metrics);
    assertValues(true, true, 0.6f);
  }
}
