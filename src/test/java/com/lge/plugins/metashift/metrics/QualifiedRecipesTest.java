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

import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the QualifiedRecipesTest class.
 *
 * @author Sung Gon Kim
 */
public class QualifiedRecipesTest {

  private Recipe recipe1;
  private Recipe recipe2;
  private QualifiedRecipes qualifiedRecipes;

  @Before
  public void setUp() {
    Recipes recipes = new Recipes();
    recipe1 = new Recipe("A-1.0.0-r0");
    recipe2 = new Recipe("B-1.0.0-r0");
    recipes.add(recipe1);
    recipes.add(recipe2);
    Criteria criteria = new Criteria(0.5, 0.5, 0.5, 0.5, 0.5, 5, 0.5, 0.5, 0.5, 0.5, 0.5);
    qualifiedRecipes = new QualifiedRecipes(recipes, new Metrics(criteria));
  }

  @Test
  public void testInitialState() {
    assertEquals(2, qualifiedRecipes.getOriginal().size());
    assertEquals(0, qualifiedRecipes.getQualified().size());
    assertEquals(0, qualifiedRecipes.getCacheAvailability().size());
    assertEquals(0, qualifiedRecipes.getCodeViolations().size());
    assertEquals(0, qualifiedRecipes.getComments().size());
    assertEquals(0, qualifiedRecipes.getComplexity().size());
    assertEquals(0, qualifiedRecipes.getCoverage().size());
    assertEquals(0, qualifiedRecipes.getDuplications().size());
    assertEquals(0, qualifiedRecipes.getMutationTest().size());
    assertEquals(0, qualifiedRecipes.getRecipeViolations().size());
    assertEquals(0, qualifiedRecipes.getTest().size());
  }

  @Test
  public void testGetCacheAvailability() {
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", true));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getCacheAvailability().size());
  }

  @Test
  public void testGetCodeViolations() {
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getCodeViolations().size());
  }

  @Test
  public void testGetComments() {
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getComments().size());
  }

  @Test
  public void testGetComplexity() {
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getComplexity().size());
  }

  @Test
  public void testGetCoverage() {
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getCoverage().size());
  }

  @Test
  public void testGetDuplications() {
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getDuplications().size());
  }

  @Test
  public void testGetMutationTest() {
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getMutationTest().size());
  }

  @Test
  public void testGetRecipeViolations() {
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getRecipeViolations().size());
  }

  @Test
  public void testGetTest() {
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertEquals(1, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getTest().size());
  }

  @Test
  public void testWithMultipleRecipes() {
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));

    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));

    assertEquals(2, qualifiedRecipes.getQualified().size());
    assertEquals(1, qualifiedRecipes.getCacheAvailability().size());
    assertEquals(1, qualifiedRecipes.getCodeViolations().size());
    assertEquals(1, qualifiedRecipes.getComments().size());
    assertEquals(1, qualifiedRecipes.getComplexity().size());
    assertEquals(1, qualifiedRecipes.getCoverage().size());
    assertEquals(1, qualifiedRecipes.getDuplications().size());
    assertEquals(1, qualifiedRecipes.getMutationTest().size());
    assertEquals(1, qualifiedRecipes.getRecipeViolations().size());
    assertEquals(1, qualifiedRecipes.getTest().size());
  }
}
