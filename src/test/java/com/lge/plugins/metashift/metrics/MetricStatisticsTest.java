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
import com.lge.plugins.metashift.models.Configuration;
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
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SummaryStatistics;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MetricStatistics class.
 *
 * @author Sung Gon Kim
 */
public class MetricStatisticsTest {

  private Configuration configuration;
  private MetricStatistics stats;
  private Recipe recipe1;
  private Recipe recipe2;
  private Recipe recipe3;
  private Recipes recipes;

  @Before
  public void setUp() {
    configuration = ConfigurationUtils.of(50, 5, false);
    stats = new MetricStatistics(configuration);
    recipe1 = new Recipe("A-1.0.0-r0");
    recipe2 = new Recipe("B-1.0.0-r0");
    recipe3 = new Recipe("C-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    recipes.add(recipe3);
  }

  private void assertValues(SummaryStatistics stats, double min, double max, double average) {
    assertEquals(min, stats.getMin(), 0.1);
    assertEquals(max, stats.getMax(), 0.1);
    assertEquals(average, stats.getAverage(), 0.1);
  }

  @Test
  public void testInitialState() {
    assertValues(stats.getPremirrorCache(), 0.0, 0.0, 0.0);
    assertValues(stats.getSharedStateCache(), 0.0, 0.0, 0.0);
    assertValues(stats.getCodeViolations(), 0.0, 0.0, 0.0);
    assertValues(stats.getComments(), 0.0, 0.0, 0.0);
    assertValues(stats.getComplexity(), 0.0, 0.0, 0.0);
    assertValues(stats.getStatementCoverage(), 0.0, 0.0, 0.0);
    assertValues(stats.getBranchCoverage(), 0.0, 0.0, 0.0);
    assertValues(stats.getDuplications(), 0.0, 0.0, 0.0);
    assertValues(stats.getMutationTest(), 0.0, 0.0, 0.0);
    assertValues(stats.getRecipeViolations(), 0.0, 0.0, 0.0);
    assertValues(stats.getTest(), 0.0, 0.0, 0.0);
  }

  @Test
  public void testGetPremirrorCache() {
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", "X", true));
    recipe2.add(new PremirrorCacheData("B-1.0.0-r0", "X", false));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getPremirrorCache(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testGetSharedStateCache() {
    recipe1.add(new SharedStateCacheData("A-1.0.0-r0", "X", false));
    recipe2.add(new SharedStateCacheData("B-1.0.0-r0", "X", true));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getSharedStateCache(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testGetCodeViolations() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 3, 1, 1));
    recipe1.add(new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 1, "r", "m", "d", "E", "t"));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "a.file", 3, 1, 1));
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getCodeViolations(), 0.3, 0.3, 0.3);
  }

  @Test
  public void testGetComments() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "b.file", 10, 1, 1));
    recipe2.add(new CommentData("B-1.0.0-r0", "a.file", 10, 0));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getComments(), 0.0, 0.5, 0.25);
  }

  @Test
  public void testGetComplexity() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe1.add(new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 100));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "a.file", 10, 1, 1));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getComplexity(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testGetStatementCoverage() {
    recipe1.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe2.add(new PassedTestData("B-B-C", "test", "test", ""));
    recipe2.add(new BranchCoverageData("B-B-C", "a.file", 1, 1, false));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getStatementCoverage(), 1.0, 1.0, 1.0);
  }

  @Test
  public void testGetBranchCoverage() {
    recipe1.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe2.add(new PassedTestData("B-B-C", "test", "test", ""));
    recipe2.add(new BranchCoverageData("B-B-C", "a.file", 1, 1, false));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getBranchCoverage(), 0.0, 0.0, 0.0);
  }

  @Test
  public void testGetDuplications() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe1.add(new DuplicationData("A-1.0.0-r0", "a.file", 10, 0, 10));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "a.file", 10, 1, 1));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 10, 0, 0));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getDuplications(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testGetMutationTest() {
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe2.add(new SkippedMutationTestData("B-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getMutationTest(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testGetRecipeViolations() {
    recipe1.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 2));
    recipe1.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe2.add(new RecipeSizeData("B-1.0.0-r0", "a.file", 2));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getRecipeViolations(), 0.5, 0.5, 0.5);
  }

  @Test
  public void testGetTest() {
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe2.add(new FailedTestData("B-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));
    stats.parse(recipes);
    assertValues(stats.getTest(), 0.0, 1.0, 0.5);
  }

  @Test
  public void testParseRecipesWithMultipleData() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", "X", true));
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe1.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, false));
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "a.file", 5, 1, 1));
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0, 0));
    recipe2.add(new RecipeSizeData("B-1.0.0-r0", "a.file", 5));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));

    stats.parse(recipes);

    assertValues(stats.getPremirrorCache(), 1.0, 1.0, 1.0);
    assertValues(stats.getSharedStateCache(), 0.0, 0.0, 0.0);
    assertValues(stats.getCodeViolations(), 0.2, 0.2, 0.2);
    assertValues(stats.getComments(), 0.5, 0.5, 0.5);
    assertValues(stats.getComplexity(), 0.0, 0.0, 0.0);
    assertValues(stats.getStatementCoverage(), 1.0, 1.0, 1.0);
    assertValues(stats.getBranchCoverage(), 0.0, 0.0, 0.0);
    assertValues(stats.getDuplications(), 0.0, 0.0, 0.0);
    assertValues(stats.getMutationTest(), 1.0, 1.0, 1.0);
    assertValues(stats.getRecipeViolations(), 0.2, 0.2, 0.2);
    assertValues(stats.getTest(), 1.0, 1.0, 1.0);
  }

  @Test
  public void testParseMetricsWithMultipleData() {
    recipe1.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe1.add(new PremirrorCacheData("A-1.0.0-r0", "X", true));
    recipe1.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe1.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe1.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, false));
    recipe1.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe1.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe2.add(new CodeSizeData("B-1.0.0-r0", "a.file", 5, 1, 1));
    recipe2.add(new InfoCodeViolationData("B-1.0.0-r0", "a.file", 1, 2, "r", "m", "d", "E", "t"));
    recipe2.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    recipe2.add(new DuplicationData("B-1.0.0-r0", "a.file", 5, 0, 0));
    recipe2.add(new RecipeSizeData("B-1.0.0-r0", "a.file", 5));
    recipe2.add(new InfoRecipeViolationData("B-1.0.0-r0", "a.file", 1, "info", "info", "info"));
    recipe3.add(new CodeSizeData("C-1.0.0-r0", "a.file", 1, 1, 1));

    List<Metrics> metrics = recipes.stream()
        .map(recipe -> new Metrics(configuration).parse(recipe))
        .collect(Collectors.toList());
    stats.parse(metrics);

    assertValues(stats.getPremirrorCache(), 1.0, 1.0, 1.0);
    assertValues(stats.getSharedStateCache(), 0.0, 0.0, 0.0);
    assertValues(stats.getCodeViolations(), 0.2, 0.2, 0.2);
    assertValues(stats.getComments(), 0.5, 0.5, 0.5);
    assertValues(stats.getComplexity(), 0.0, 0.0, 0.0);
    assertValues(stats.getStatementCoverage(), 1.0, 1.0, 1.0);
    assertValues(stats.getBranchCoverage(), 0.0, 0.0, 0.0);
    assertValues(stats.getDuplications(), 0.0, 0.0, 0.0);
    assertValues(stats.getMutationTest(), 1.0, 1.0, 1.0);
    assertValues(stats.getRecipeViolations(), 0.2, 0.2, 0.2);
    assertValues(stats.getTest(), 1.0, 1.0, 1.0);
  }
}
