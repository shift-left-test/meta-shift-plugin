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

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.MetricDataSummary;
import com.lge.plugins.metashift.models.RecipeEvaluationSummary;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the RecipeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluatorTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private RecipeEvaluator evaluator;
  private String recipe;

  @Before
  public void setUp() throws IOException, InterruptedException {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.createDirectory("source");
    File report = utils.createDirectory("report");
    FakeReportBuilder builder = new FakeReportBuilder();
    builder.add(new FakeRecipe(source).setPremirror(1, 1).setSharedState(1, 1)
        .add(new FakeScript(10).setIssues(1, 1, 1))
        .add(new FakeSource(10, 5, 5, 5)
            .setCodeViolations(1, 1, 1)
            .setComplexity(10, 1, 1)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1)
        )
    );
    builder.add(new FakeRecipe(source).setPremirror(1, 1).setSharedState(1, 1)
        .add(new FakeScript(10).setIssues(1, 1, 1))
        .add(new FakeSource(10, 5, 5, 5)
            .setCodeViolations(1, 1, 1)
            .setComplexity(10, 1, 1)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1)
        )
    );
    builder.toFile(report);

    Configuration configuration = new Configuration();
    configuration.setPremirrorCacheThreshold(50);
    configuration.setSharedStateCacheThreshold(50);
    configuration.setRecipeViolationThreshold(0.5);
    configuration.setCommentThreshold(50);
    configuration.setCodeViolationThreshold(0.5);
    configuration.setComplexityTolerance(5);
    configuration.setComplexityThreshold(50);
    configuration.setDuplicationTolerance(5);
    configuration.setDuplicationThreshold(50);
    configuration.setTestThreshold(50);
    configuration.setStatementCoverageThreshold(50);
    configuration.setBranchCoverageThreshold(50);
    configuration.setMutationTestThreshold(50);

    evaluator = new RecipeEvaluator(configuration);
    evaluator.parse(new Recipes(new FilePath(report)));
    recipe = evaluator.getKeys().get(0);
  }

  private void assertLinesOfCode(LinesOfCode o, long lines, long files, long recipes) {
    assertEquals(lines, o.getLines());
    assertEquals(files, o.getFiles());
    assertEquals(recipes, o.getRecipes());
  }

  private void assertEvaluation(Evaluation o, boolean qualified, double ratio) {
    assertEquals(qualified, o.isQualified());
    assertEquals(ratio, o.getRatio(), 0.01);
  }

  private void assertStatistics(Statistics o, double min, double average, double max) {
    assertEquals(min, o.getMin(), 0.01);
    assertEquals(average, o.getAverage(), 0.01);
    assertEquals(max, o.getMax(), 0.01);
  }

  private void assertDistribution(Distribution o, long first, long second, long third,
      long fourth) {
    assertEquals(first, o.getFirst().getCount());
    assertEquals(second, o.getSecond().getCount());
    assertEquals(third, o.getThird().getCount());
    assertEquals(fourth, o.getFourth().getCount());
  }

  @Test
  public void testSize() {
    assertEquals(2, evaluator.getKeys().size());
  }

  @Test
  public void testGetLinesOfCodeOfAll() {
    assertLinesOfCode(evaluator.getLinesOfCode(), 20, 2, 2);
  }

  @Test
  public void testGetLinesOfCodeOfRecipe() {
    assertLinesOfCode(evaluator.getLinesOfCode(recipe), 10, 1, 1);
  }

  @Test
  public void testGetPremirrorCacheOfAll() {
    MetricDataSummary summary = evaluator.getPremirrorCache();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 2, 2, 0, 0);
  }

  @Test
  public void testGetPremirrorCacheOfRecipe() {
    MetricDataSummary summary = evaluator.getPremirrorCache(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 1, 1, 0, 0);
  }

  @Test
  public void testGetSharedStateCacheOfAll() {
    MetricDataSummary summary = evaluator.getSharedStateCache();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 2, 2, 0, 0);
  }

  @Test
  public void testGetSharedStateCacheOfRecipe() {
    MetricDataSummary summary = evaluator.getSharedStateCache(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 1, 1, 0, 0);
  }

  @Test
  public void testGetRecipeViolationsOfAll() {
    MetricDataSummary summary = evaluator.getRecipeViolations();
    assertEvaluation(summary.getEvaluation(), true, 0.3);
    assertStatistics(summary.getStatistics(), 0.3, 0.3, 0.3);
    assertDistribution(summary.getDistribution(), 2, 2, 2, 0);
  }

  @Test
  public void testGetRecipeViolationsOfRecipe() {
    MetricDataSummary summary = evaluator.getRecipeViolations(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.3);
    assertStatistics(summary.getStatistics(), 0.3, 0.3, 0.3);
    assertDistribution(summary.getDistribution(), 1, 1, 1, 0);
  }

  @Test
  public void testGetCommentsOfAll() {
    MetricDataSummary summary = evaluator.getComments();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 10, 10, 0, 0);
  }

  @Test
  public void testGetCommentsOfRecipe() {
    MetricDataSummary summary = evaluator.getComments(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 5, 5, 0, 0);
  }

  @Test
  public void testGetCodeViolationsOfAll() {
    MetricDataSummary summary = evaluator.getCodeViolations();
    assertEvaluation(summary.getEvaluation(), true, 0.3);
    assertStatistics(summary.getStatistics(), 0.3, 0.3, 0.3);
    assertDistribution(summary.getDistribution(), 2, 2, 2, 0);
  }

  @Test
  public void testGetCodeViolationsOfRecipe() {
    MetricDataSummary summary = evaluator.getCodeViolations(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.3);
    assertStatistics(summary.getStatistics(), 0.3, 0.3, 0.3);
    assertDistribution(summary.getDistribution(), 1, 1, 1, 0);
  }

  @Test
  public void testGetComplexityOfAll() {
    MetricDataSummary summary = evaluator.getComplexity();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 2, 2, 0, 0);
  }

  @Test
  public void testGetComplexityOfRecipe() {
    MetricDataSummary summary = evaluator.getComplexity(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 1, 1, 0, 0);
  }

  @Test
  public void testGetDuplicationsOfAll() {
    MetricDataSummary summary = evaluator.getDuplications();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 10, 10, 0, 0);
  }

  @Test
  public void testGetDuplicationsOfRecipe() {
    MetricDataSummary summary = evaluator.getDuplications(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 5, 5, 0, 0);
  }

  @Test
  public void testGetUnitTestsOfAll() {
    MetricDataSummary summary = evaluator.getUnitTests();
    assertEvaluation(summary.getEvaluation(), false, 0.25);
    assertStatistics(summary.getStatistics(), 0.25, 0.25, 0.25);
    assertDistribution(summary.getDistribution(), 2, 2, 2, 2);
  }

  @Test
  public void testGetUnitTestsOfRecipe() {
    MetricDataSummary summary = evaluator.getUnitTests(recipe);
    assertEvaluation(summary.getEvaluation(), false, 0.25);
    assertStatistics(summary.getStatistics(), 0.25, 0.25, 0.25);
    assertDistribution(summary.getDistribution(), 1, 1, 1, 1);
  }

  @Test
  public void testGetStatementCoverageOfAll() {
    MetricDataSummary summary = evaluator.getStatementCoverage();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 2, 2, 0, 0);
  }

  @Test
  public void testGetStatementCoverageOfRecipe() {
    MetricDataSummary summary = evaluator.getStatementCoverage(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 1, 1, 0, 0);
  }

  @Test
  public void testGetBranchCoverageOfAll() {
    MetricDataSummary summary = evaluator.getBranchCoverage();
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 2, 2, 0, 0);
  }

  @Test
  public void testGetBranchCoverageOfRecipe() {
    MetricDataSummary summary = evaluator.getBranchCoverage(recipe);
    assertEvaluation(summary.getEvaluation(), true, 0.5);
    assertStatistics(summary.getStatistics(), 0.5, 0.5, 0.5);
    assertDistribution(summary.getDistribution(), 1, 1, 0, 0);
  }

  @Test
  public void testGetMutationTestsOfAll() {
    MetricDataSummary summary = evaluator.getMutationTests();
    assertEvaluation(summary.getEvaluation(), false, 0.33);
    assertStatistics(summary.getStatistics(), 0.33, 0.33, 0.33);
    assertDistribution(summary.getDistribution(), 2, 2, 2, 0);
  }

  @Test
  public void testGetMutationTestsOfRecipe() {
    MetricDataSummary summary = evaluator.getMutationTests(recipe);
    assertEvaluation(summary.getEvaluation(), false, 0.33);
    assertStatistics(summary.getStatistics(), 0.33, 0.33, 0.33);
    assertDistribution(summary.getDistribution(), 1, 1, 1, 0);
  }

  @Test
  public void testGetEvaluationSummaryOfAll() {
    RecipeEvaluationSummary summary = evaluator.getEvaluationSummary();
    assertEvaluation(summary.getMutationTests().getEvaluation(), false, 0.33);
    assertStatistics(summary.getMutationTests().getStatistics(), 0.33, 0.33, 0.33);
    assertDistribution(summary.getMutationTests().getDistribution(), 2, 2, 2, 0);
  }

  @Test
  public void testGetEvaluationSummaryOfRecipe() {
    RecipeEvaluationSummary summary = evaluator.getEvaluationSummary(recipe);
    assertEvaluation(summary.getMutationTests().getEvaluation(), false, 0.33);
    assertStatistics(summary.getMutationTests().getStatistics(), 0.33, 0.33, 0.33);
    assertDistribution(summary.getMutationTests().getDistribution(), 1, 1, 1, 0);
  }
}
