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

package com.lge.plugins.metashift.metrics2;

import static com.lge.plugins.metashift.metrics2.TestUtils.assertCounter;
import static com.lge.plugins.metashift.metrics2.TestUtils.assertEvaluator;
import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.SizeData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
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
    Criteria criteria = new Criteria(0.5, 0.5, 0.5, 0.5, 0.5, 5, 0.5, 0.5, 0.5, 0.5, 0.5);
    metrics = new Metrics(criteria);
    recipe = new Recipe("A-B-C");
    recipes = new RecipeList();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(metrics, false, false);
    assertCounter(metrics, 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipes() {
    metrics.parse(recipes);

    assertEvaluator(metrics, false, false);
    assertCounter(metrics, 0, 0, 0.0);
  }

  @Test
  public void testParseWithUnqualifiedCacheData() {
    recipe.add(new PremirrorCacheData("A", false));
    recipe.add(new SharedStateCacheData("B", "do_package", false));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getCacheAvailability(), true, false);
    assertCounter(metrics.getCacheAvailability(), 2, 0, 0.0);
  }

  @Test
  public void testParseWithQualifiedCacheData() {
    recipe.add(new PremirrorCacheData("A", true));
    recipe.add(new SharedStateCacheData("B", "do_package", true));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getCacheAvailability(), true, true);
    assertCounter(metrics.getCacheAvailability(), 2, 2, 1.0);
  }

  @Test
  public void testParseWithUnqualifiedCodeViolationData() {
    recipe.add(new MajorCodeViolationData("A", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getCodeViolations(), true, false);
    assertCounter(metrics.getCodeViolations(), 1, 1, 1.0);
  }

  @Test
  public void testParseWithQualifiedCodeViolationData() {
    recipe.add(new MajorCodeViolationData("A", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(new MinorCodeViolationData("A", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(new InfoCodeViolationData("A", "c.file", 1, 2, "rule", "m", "d", "E", "t"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getCodeViolations(), true, true);
    assertCounter(metrics.getCodeViolations(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithUnqualifiedCommentData() {
    recipe.add(new CommentData("A", "a.file", 10, 5));
    recipe.add(new CommentData("B", "b.file", 10, 0));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getComments(), true, false);
    assertCounter(metrics.getComments(), 20, 5, 0.25);
  }

  @Test
  public void testParseWithQualifiedCommentData() {
    recipe.add(new CommentData("A", "a.file", 10, 5));
    recipe.add(new CommentData("B", "b.file", 20, 15));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getComments(), true, true);
    assertCounter(metrics.getComments(), 30, 20, 0.66);
  }

  @Test
  public void testParseWithUnqualifiedComplexityData() {
    recipe.add(new ComplexityData("A", "a.file", "f()", 5, 10, 5));
    recipe.add(new ComplexityData("B", "a.file", "f()", 5, 10, 5));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getComplexity(), true, false);
    assertCounter(metrics.getComplexity(), 2, 2, 1.0);
  }

  @Test
  public void testParseWithQualifiedComplexityData() {
    recipe.add(new ComplexityData("A", "a.file", "f()", 5, 10, 5));
    recipe.add(new ComplexityData("B", "a.file", "f()", 5, 10, 0));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getComplexity(), true, true);
    assertCounter(metrics.getComplexity(), 2, 1, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedCoverageData() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", "func1()", 1, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", "func1()", 1, 1, false));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getCoverage(), true, false);
    assertCounter(metrics.getCoverage(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedCoverageData() {
    recipe.add(new StatementCoverageData("A-B-C", "a.file", "func1()", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", "func1()", 1, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", "func1()", 1, 1, true));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getCoverage(), true, true);
    assertCounter(metrics.getCoverage(), 3, 2, 0.6);
  }

  @Test
  public void testParseWithUnqualifiedDuplicationData() {
    recipe.add(new DuplicationData("A-1.0.0-r0", "a.file", 5, 0));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getDuplications(), true, false);
    assertCounter(metrics.getDuplications(), 15, 10, 0.6);
  }

  @Test
  public void testParseWithQualifiedDuplicationData() {
    recipe.add(new DuplicationData("A-1.0.0-r0", "a.file", 20, 0));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getDuplications(), true, true);
    assertCounter(metrics.getDuplications(), 30, 10, 0.3);
  }

  @Test
  public void testParseWithUnqualifiedMutationTestData() {
    recipe.add(new SkippedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getMutationTest(), true, false);
    assertCounter(metrics.getMutationTest(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedMutationTestData() {
    recipe.add(new SkippedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A", "d.file", "C", "f()", 1, "AOR", "TC"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getMutationTest(), true, true);
    assertCounter(metrics.getMutationTest(), 4, 2, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedRecipeViolationData() {
    recipe.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new MajorRecipeViolationData("A", "b.file", 1, "major", "major", "major"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getRecipeViolations(), true, false);
    assertCounter(metrics.getRecipeViolations(), 3, 2, 0.6);
  }

  @Test
  public void testParseWithQualifiedRecipeViolationData() {
    recipe.add(new MajorRecipeViolationData("A", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new InfoRecipeViolationData("A", "a.file", 1, "info", "info", "info"));
    recipe.add(new MajorRecipeViolationData("A", "b.file", 1, "major", "major", "major"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getRecipeViolations(), true, true);
    assertCounter(metrics.getRecipeViolations(), 4, 2, 0.5);
  }

  @Test
  public void testParseWithSizeData() {
    recipe.add(new SizeData("A", "a.file", 3, 2, 1));
    recipe.add(new SizeData("A", "b.file", 6, 4, 2));
    metrics.parse(recipes);

    assertEvaluator(metrics, false, false);
    assertCounter(metrics, 0, 0, 0.0);
    assertEquals(1, metrics.getSize().getRecipes());
    assertEquals(2, metrics.getSize().getFiles());
    assertEquals(9, metrics.getSize().getLines());
    assertEquals(6, metrics.getSize().getFunctions());
    assertEquals(3, metrics.getSize().getClasses());
  }

  @Test
  public void testParseWithUnqualifiedTestData() {
    recipe.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A", "a.suite", "c.tc", "msg"));
    recipe.add(new SkippedTestData("A", "d.suite", "c.tc", "msg"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, false);
    assertCounter(metrics, 1, 0, 0.0);
    assertEvaluator(metrics.getTest(), true, false);
    assertCounter(metrics.getTest(), 4, 1, 0.25);
  }

  @Test
  public void testParseWithQualifiedTestData() {
    recipe.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A", "a.suite", "c.tc", "msg"));
    recipe.add(new PassedTestData("A", "e.suite", "a.tc", "msg"));
    metrics.parse(recipes);

    assertEvaluator(metrics, true, true);
    assertCounter(metrics, 1, 1, 1.0);
    assertEvaluator(metrics.getTest(), true, true);
    assertCounter(metrics.getTest(), 4, 2, 0.5);
  }
}
