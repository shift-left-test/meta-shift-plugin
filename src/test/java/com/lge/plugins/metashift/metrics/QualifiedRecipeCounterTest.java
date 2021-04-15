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

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the QualifiedRecipeCounterTest class.
 *
 * @author Sung Gon Kim
 */
public class QualifiedRecipeCounterTest {

  private QualifiedRecipeCounter counter;
  private Recipe recipe1;
  private Recipe recipe2;
  private Recipe recipe3;
  private Recipes recipes;

  @Before
  public void setUp() {
    Criteria criteria = new Criteria(0.5, 0.5, 0.5, 0.5, 0.5, 5, 0.5, 0.5, 0.5, 0.5, 0.5);
    counter = new QualifiedRecipeCounter(criteria);
    recipe1 = new Recipe("A-1.0.0-r0");
    recipe2 = new Recipe("B-1.0.0-r0");
    recipe3 = new Recipe("C-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    recipes.add(recipe3);
  }

  private void assertValues(Counter counter, int denominator, int numerator) {
    assertEquals(denominator, counter.getDenominator());
    assertEquals(numerator, counter.getNumerator());
  }

  @Test
  public void testInitialState() {
    assertValues(counter.getCacheAvailability(), 0, 0);
    assertValues(counter.getCodeViolations(), 0, 0);
    assertValues(counter.getComments(), 0, 0);
    assertValues(counter.getComplexity(), 0, 0);
    assertValues(counter.getCoverage(), 0, 0);
    assertValues(counter.getDuplications(), 0, 0);
    assertValues(counter.getMutationTest(), 0, 0);
    assertValues(counter.getRecipeViolations(), 0, 0);
    assertValues(counter.getTest(), 0, 0);
    assertValues(counter.getRecipes(), 0, 0);
  }

  @Test
  public void testGetCacheAvailability() {
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe2.add(new SharedStateCacheData("B-1.0.0-r0", "do_X", false));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getCacheAvailability(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetCodeViolations() {
    recipe1.add(new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getCodeViolations(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetComments() {
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe2.add(new CommentData("B-1.0.0-r0", "a.file", 10, 0));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getComments(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetComplexity() {
    recipe1.add(new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 100));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getComplexity(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetCoverage() {
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    recipe2.add(new BranchCoverageData("B-B-C", "a.file", "func1()", 1, 1, false));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getCoverage(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetDuplications() {
    recipe1.add(new DuplicationData("A-1.0.0-r0", "a.file", 5, 5));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getDuplications(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetMutationTest() {
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe2.add(new SkippedMutationTestData("B-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getMutationTest(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetRecipeViolations() {
    recipe1.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getRecipeViolations(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testGetTest() {
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe2.add(new FailedTestData("B-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getTest(), 2, 1);
    assertValues(counter.getRecipes(), 2, 1);
  }

  @Test
  public void testWithMultipleData() {
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", true));
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    counter.parse(recipes);
    assertValues(counter.getCacheAvailability(), 1, 1);
    assertValues(counter.getCodeViolations(), 1, 1);
    assertValues(counter.getComments(), 1, 1);
    assertValues(counter.getComplexity(), 1, 1);
    assertValues(counter.getCoverage(), 1, 1);
    assertValues(counter.getDuplications(), 1, 1);
    assertValues(counter.getMutationTest(), 1, 1);
    assertValues(counter.getRecipeViolations(), 1, 1);
    assertValues(counter.getTest(), 1, 1);
    assertValues(counter.getRecipes(), 2, 2);
  }
}
