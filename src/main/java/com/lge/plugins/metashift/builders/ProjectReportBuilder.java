/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils.Function;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
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

  private Void addLinesOfCode(Recipes recipes) throws IOException {
    LinesOfCode linesOfCode = new LinesOfCodeCollector().parse(recipes);
    put(Metric.NONE, Data.LINES_OF_CODE, JSONObject.fromObject(linesOfCode));
    return null;
  }

  private Void addPremirrorCache(Recipes recipes) throws IOException {
    add(Metric.PREMIRROR_CACHE,
        new PremirrorCacheEvaluator(configuration),
        new PremirrorCacheCounter(),
        new PremirrorCacheDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addSharedStateCache(Recipes recipes) throws IOException {
    add(Metric.SHARED_STATE_CACHE,
        new SharedStateCacheEvaluator(configuration),
        new SharedStateCacheCounter(),
        new SharedStateCacheDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addRecipeViolations(Recipes recipes) throws IOException {
    add(Metric.RECIPE_VIOLATIONS,
        new RecipeViolationEvaluator(configuration),
        new RecipeViolationCounter(),
        new RecipeViolationDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addComments(Recipes recipes) throws IOException {
    add(Metric.COMMENTS,
        new CommentEvaluator(configuration),
        new CommentCounter(),
        new CommentDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addCodeViolations(Recipes recipes) throws IOException {
    add(Metric.CODE_VIOLATIONS,
        new CodeViolationEvaluator(configuration),
        new CodeViolationCounter(),
        new CodeViolationDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addComplexity(Recipes recipes) throws IOException {
    add(Metric.COMPLEXITY,
        new ComplexityEvaluator(configuration),
        new ComplexityCounter(configuration),
        new ComplexityDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addDuplications(Recipes recipes) throws IOException {
    add(Metric.DUPLICATIONS,
        new DuplicationEvaluator(configuration),
        new DuplicationCounter(configuration),
        new DuplicationDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addTestedRecipes(Recipes recipes) throws IOException {
    Evaluation evaluation = new TestedRecipeEvaluator().parse(recipes);
    put(Metric.TESTED_RECIPES, Data.EVALUATION, JSONObject.fromObject(evaluation));
    return null;
  }

  private Void addUnitTests(Recipes recipes) throws IOException {
    add(Metric.UNIT_TESTS,
        new UnitTestEvaluator(configuration),
        new UnitTestCounter(),
        new UnitTestDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addStatementCoverage(Recipes recipes) throws IOException {
    add(Metric.STATEMENT_COVERAGE,
        new StatementCoverageEvaluator(configuration),
        new StatementCoverageCounter(),
        new StatementCoverageDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addBranchCoverage(Recipes recipes) throws IOException {
    add(Metric.BRANCH_COVERAGE,
        new BranchCoverageEvaluator(configuration),
        new BranchCoverageCounter(),
        new BranchCoverageDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addMutationTests(Recipes recipes) throws IOException {
    add(Metric.MUTATION_TESTS,
        new MutationTestEvaluator(configuration),
        new MutationTestCounter(),
        new MutationTestDataSummaryAggregator(configuration),
        recipes);
    return null;
  }

  private Void addTreemap(Recipes recipes) throws IOException {
    Evaluator evaluator = new RecipeEvaluator(configuration);
    List<TreemapData> objects = new TreemapDataAggregator(evaluator).parse(recipes);
    put(Metric.NONE, Data.TREEMAP, JSONArray.fromObject(objects));
    return null;
  }

  private Void addSummaries(Recipes recipes) throws IOException {
    List<EvaluationSummary> objects = new EvaluationSummaryAggregator(configuration).parse(recipes);
    put(Metric.NONE, Data.SUMMARIES, JSONArray.fromObject(objects));
    return null;
  }

  private Callable<Void> newTask(Function<Recipes, Void> functor, Recipes recipes) {
    return () -> functor.apply(recipes);
  }

  @Override
  public ProjectReport parse(Recipes recipes) throws IOException, InterruptedException {
    ExecutorServiceUtils.invokeAll(
        newTask(this::addLinesOfCode, recipes),
        newTask(this::addPremirrorCache, recipes),
        newTask(this::addSharedStateCache, recipes),
        newTask(this::addRecipeViolations, recipes),
        newTask(this::addComments, recipes),
        newTask(this::addCodeViolations, recipes),
        newTask(this::addComplexity, recipes),
        newTask(this::addDuplications, recipes),
        newTask(this::addTestedRecipes, recipes),
        newTask(this::addUnitTests, recipes),
        newTask(this::addStatementCoverage, recipes),
        newTask(this::addBranchCoverage, recipes),
        newTask(this::addMutationTests, recipes),
        newTask(this::addTreemap, recipes),
        newTask(this::addSummaries, recipes)
    );
    return new ProjectReport(dataSource);
  }
}
