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

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.aggregators.Aggregator;
import com.lge.plugins.metashift.aggregators.BranchCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.CodeViolationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.CommentDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.ComplexityDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.DuplicationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.EvaluationSummaryAggregator;
import com.lge.plugins.metashift.aggregators.MutationTestDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.PremirrorCacheDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.RecipeViolationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.SharedStateCacheDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.StatementCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.TreemapDataAggregator;
import com.lge.plugins.metashift.aggregators.UnitTestDataSummaryAggregator;
import com.lge.plugins.metashift.analysis.BranchCoverageCounter;
import com.lge.plugins.metashift.analysis.BranchCoverageEvaluator;
import com.lge.plugins.metashift.analysis.CodeViolationCounter;
import com.lge.plugins.metashift.analysis.CodeViolationEvaluator;
import com.lge.plugins.metashift.analysis.CommentCounter;
import com.lge.plugins.metashift.analysis.CommentEvaluator;
import com.lge.plugins.metashift.analysis.ComplexityCounter;
import com.lge.plugins.metashift.analysis.ComplexityEvaluator;
import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.DuplicationCounter;
import com.lge.plugins.metashift.analysis.DuplicationEvaluator;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.analysis.MutationTestCounter;
import com.lge.plugins.metashift.analysis.MutationTestEvaluator;
import com.lge.plugins.metashift.analysis.PremirrorCacheCounter;
import com.lge.plugins.metashift.analysis.PremirrorCacheEvaluator;
import com.lge.plugins.metashift.analysis.RecipeEvaluator;
import com.lge.plugins.metashift.analysis.RecipeViolationCounter;
import com.lge.plugins.metashift.analysis.RecipeViolationEvaluator;
import com.lge.plugins.metashift.analysis.SharedStateCacheCounter;
import com.lge.plugins.metashift.analysis.SharedStateCacheEvaluator;
import com.lge.plugins.metashift.analysis.StatementCoverageCounter;
import com.lge.plugins.metashift.analysis.StatementCoverageEvaluator;
import com.lge.plugins.metashift.analysis.StatisticsCollector;
import com.lge.plugins.metashift.analysis.TestedRecipeEvaluator;
import com.lge.plugins.metashift.analysis.UnitTestCounter;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
import com.lge.plugins.metashift.models.TreemapData;
import com.lge.plugins.metashift.persistence.DataSource;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ProjectReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class ProjectReportBuilder implements Builder<Recipes, ProjectReport> {

  private final Configuration configuration;
  private final DataSource dataSource;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   * @param dataSource    for persistent objects
   */
  public ProjectReportBuilder(Configuration configuration, DataSource dataSource) {
    this.configuration = configuration;
    this.dataSource = dataSource;
  }

  private <T> void put(Metric metric, Data data, T object) throws IOException {
    dataSource.put(object, Scope.PROJECT.name(), metric.name(), data.name());
  }

  private void addLinesOfCode(Recipes recipes) throws IOException {
    LinesOfCode linesOfCode = new LinesOfCodeCollector().parse(recipes);
    put(Metric.NONE, Data.LINES_OF_CODE, JSONObject.fromObject(linesOfCode));
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private void add(Metric metric, Evaluator evaluator, Counter counter,
      Aggregator<DataSummary> aggregator, Recipes recipes) throws IOException {
    Evaluation evaluation = evaluator.parse(recipes);
    put(metric, Data.EVALUATION, JSONObject.fromObject(evaluation));
    Statistics statistics = new StatisticsCollector(evaluator).parse(recipes);
    put(metric, Data.STATISTICS, JSONObject.fromObject(statistics));
    Distribution distribution = counter.parse(recipes);
    put(metric, Data.DISTRIBUTION, JSONObject.fromObject(distribution));
    List<TreemapData> treemap = new TreemapDataAggregator(evaluator).parse(recipes);
    put(metric, Data.TREEMAP, JSONArray.fromObject(treemap));
    List<DataSummary> summaries = aggregator.parse(recipes);
    put(metric, Data.SUMMARIES, JSONArray.fromObject(summaries));
  }

  private void addPremirrorCache(Recipes recipes) throws IOException {
    add(Metric.PREMIRROR_CACHE,
        new PremirrorCacheEvaluator(configuration),
        new PremirrorCacheCounter(),
        new PremirrorCacheDataSummaryAggregator(configuration),
        recipes);
  }

  private void addSharedStateCache(Recipes recipes) throws IOException {
    add(Metric.SHARED_STATE_CACHE,
        new SharedStateCacheEvaluator(configuration),
        new SharedStateCacheCounter(),
        new SharedStateCacheDataSummaryAggregator(configuration),
        recipes);
  }

  private void addRecipeViolations(Recipes recipes) throws IOException {
    add(Metric.RECIPE_VIOLATIONS,
        new RecipeViolationEvaluator(configuration),
        new RecipeViolationCounter(),
        new RecipeViolationDataSummaryAggregator(configuration),
        recipes);
  }

  private void addComments(Recipes recipes) throws IOException {
    add(Metric.COMMENTS,
        new CommentEvaluator(configuration),
        new CommentCounter(),
        new CommentDataSummaryAggregator(configuration),
        recipes);
  }

  private void addCodeViolations(Recipes recipes) throws IOException {
    add(Metric.CODE_VIOLATIONS,
        new CodeViolationEvaluator(configuration),
        new CodeViolationCounter(),
        new CodeViolationDataSummaryAggregator(configuration),
        recipes);
  }

  private void addComplexity(Recipes recipes) throws IOException {
    add(Metric.COMPLEXITY,
        new ComplexityEvaluator(configuration),
        new ComplexityCounter(configuration),
        new ComplexityDataSummaryAggregator(configuration),
        recipes);
  }

  private void addDuplications(Recipes recipes) throws IOException {
    add(Metric.DUPLICATIONS,
        new DuplicationEvaluator(configuration),
        new DuplicationCounter(configuration),
        new DuplicationDataSummaryAggregator(configuration),
        recipes);
  }

  private void addTestedRecipes(Recipes recipes) throws IOException {
    Evaluation evaluation = new TestedRecipeEvaluator().parse(recipes);
    put(Metric.TESTED_RECIPES, Data.EVALUATION, JSONObject.fromObject(evaluation));
  }

  private void addUnitTests(Recipes recipes) throws IOException {
    add(Metric.UNIT_TESTS,
        new UnitTestEvaluator(configuration),
        new UnitTestCounter(),
        new UnitTestDataSummaryAggregator(configuration),
        recipes);
  }

  private void addStatementCoverage(Recipes recipes) throws IOException {
    add(Metric.STATEMENT_COVERAGE,
        new StatementCoverageEvaluator(configuration),
        new StatementCoverageCounter(),
        new StatementCoverageDataSummaryAggregator(configuration),
        recipes);
  }

  private void addBranchCoverage(Recipes recipes) throws IOException {
    add(Metric.BRANCH_COVERAGE,
        new BranchCoverageEvaluator(configuration),
        new BranchCoverageCounter(),
        new BranchCoverageDataSummaryAggregator(configuration),
        recipes);
  }

  private void addMutationTests(Recipes recipes) throws IOException {
    add(Metric.MUTATION_TESTS,
        new MutationTestEvaluator(configuration),
        new MutationTestCounter(),
        new MutationTestDataSummaryAggregator(configuration),
        recipes);
  }

  private void addTreemap(Recipes recipes) throws IOException {
    Evaluator evaluator = new RecipeEvaluator(configuration);
    List<TreemapData> objects = new TreemapDataAggregator(evaluator).parse(recipes);
    put(Metric.NONE, Data.TREEMAP, JSONArray.fromObject(objects));
  }

  private void addSummaries(Recipes recipes) throws IOException {
    List<EvaluationSummary> objects = new EvaluationSummaryAggregator(configuration).parse(recipes);
    put(Metric.NONE, Data.SUMMARIES, JSONArray.fromObject(objects));
  }

  @Override
  public ProjectReport parse(Recipes recipes) throws IOException, InterruptedException {
    addLinesOfCode(recipes);

    addPremirrorCache(recipes);
    addSharedStateCache(recipes);
    addRecipeViolations(recipes);
    addComments(recipes);
    addCodeViolations(recipes);
    addComplexity(recipes);
    addDuplications(recipes);
    addTestedRecipes(recipes);
    addUnitTests(recipes);
    addStatementCoverage(recipes);
    addBranchCoverage(recipes);
    addMutationTests(recipes);

    addTreemap(recipes);
    addSummaries(recipes);

    return new ProjectReport(dataSource);
  }
}
