/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.aggregators.Aggregator;
import com.lge.plugins.metashift.aggregators.BranchCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.EvaluationSummaryAggregator;
import com.lge.plugins.metashift.aggregators.MutationTestDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.StatementCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.UnitTestDataSummaryAggregator;
import com.lge.plugins.metashift.analysis.BranchCoverageCounter;
import com.lge.plugins.metashift.analysis.BranchCoverageEvaluator;
import com.lge.plugins.metashift.analysis.Counter;
import com.lge.plugins.metashift.analysis.Evaluator;
import com.lge.plugins.metashift.analysis.MutationTestCounter;
import com.lge.plugins.metashift.analysis.MutationTestEvaluator;
import com.lge.plugins.metashift.analysis.StatementCoverageCounter;
import com.lge.plugins.metashift.analysis.StatementCoverageEvaluator;
import com.lge.plugins.metashift.analysis.StatisticsCollector;
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
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
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
    List<DataSummary> summaries = aggregator.parse(recipes);
    put(metric, Data.SUMMARIES, JSONArray.fromObject(summaries));
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
        newTask(this::addUnitTests, recipes),
        newTask(this::addStatementCoverage, recipes),
        newTask(this::addBranchCoverage, recipes),
        newTask(this::addMutationTests, recipes),
        newTask(this::addSummaries, recipes)
    );
    return new ProjectReport(dataSource);
  }
}
