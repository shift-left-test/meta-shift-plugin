/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.aggregators.BranchCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.MutationTestDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.RecipeAggregator;
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
import com.lge.plugins.metashift.analysis.UnitTestCounter;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils.Function;
import hudson.FilePath;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * RecipeReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class RecipeReportBuilder implements Builder<Recipe, RecipeReport> {

  private final Configuration configuration;
  private final DataSource dataSource;
  private final FilePath path;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   * @param dataSource    for persistent objects
   * @param path          to the report directory
   */
  public RecipeReportBuilder(Configuration configuration, DataSource dataSource, FilePath path) {
    this.configuration = configuration;
    this.dataSource = dataSource;
    this.path = path;
  }

  private <T> void put(Metric metric, Data data, String recipe, T object) throws IOException {
    dataSource.put(object, Scope.RECIPE.name(), metric.name(), data.name(), recipe);
  }

  private <T> void put(Metric metric, Data data, String recipe, String file, T object)
      throws IOException {
    dataSource.put(object, Scope.RECIPE.name(), metric.name(), data.name(), recipe, file);
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private void add(Metric metric, Evaluator evaluator, Counter counter,
      RecipeAggregator<?> aggregator, Recipe recipe) throws IOException {
    Evaluation evaluation = evaluator.parse(recipe);
    put(metric, Data.EVALUATION, recipe.getName(), JSONObject.fromObject(evaluation));
    Distribution distribution = counter.parse(recipe);
    put(metric, Data.DISTRIBUTION, recipe.getName(), JSONObject.fromObject(distribution));
    List<?> summaries = aggregator.parse(recipe);
    put(metric, Data.SUMMARIES, recipe.getName(), JSONArray.fromObject(summaries));
  }

  private <T> Void addObjects(Metric metric, String recipe, Map<String, List<T>> group)
      throws IOException {
    for (Entry<String, List<T>> entry : group.entrySet()) {
      put(metric, Data.OBJECTS, recipe, entry.getKey(), JSONArray.fromObject(entry.getValue()));
    }
    return null;
  }

  private Void addUnitTests(Recipe recipe) throws IOException {
    add(Metric.UNIT_TESTS,
        new UnitTestEvaluator(configuration),
        new UnitTestCounter(),
        new UnitTestDataSummaryAggregator(configuration),
        recipe);
    return null;
  }

  private Void addStatementCoverage(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.STATEMENT_COVERAGE,
        new StatementCoverageEvaluator(configuration),
        new StatementCoverageCounter(),
        new StatementCoverageDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<CoverageData>> group = recipe.objects(StatementCoverageData.class)
        .collect(Collectors.groupingBy(CoverageData::getFile));
    addObjects(Metric.STATEMENT_COVERAGE, recipe.getName(), group);
    return null;
  }

  private Void addBranchCoverage(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.BRANCH_COVERAGE,
        new BranchCoverageEvaluator(configuration),
        new BranchCoverageCounter(),
        new BranchCoverageDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<CoverageData>> group = recipe.objects(BranchCoverageData.class)
        .collect(Collectors.groupingBy(CoverageData::getFile));
    addObjects(Metric.BRANCH_COVERAGE, recipe.getName(), group);
    return null;
  }

  private Void addMutationTests(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.MUTATION_TESTS,
        new MutationTestEvaluator(configuration),
        new MutationTestCounter(),
        new MutationTestDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<MutationTestData>> group = recipe.objects(MutationTestData.class)
        .collect(Collectors.groupingBy(MutationTestData::getFile));
    addObjects(Metric.MUTATION_TESTS, recipe.getName(), group);
    return null;
  }

  private Callable<Void> newTask(Function<Recipe, Void> functor, Recipe recipe) {
    return () -> functor.apply(recipe);
  }

  @Override
  public RecipeReport parse(Recipe recipe) throws IOException, InterruptedException {
    ExecutorServiceUtils.invokeAll(
        newTask(this::addUnitTests, recipe),
        newTask(this::addStatementCoverage, recipe),
        newTask(this::addBranchCoverage, recipe),
        newTask(this::addMutationTests, recipe)
    );
    return new RecipeReport(dataSource, recipe.getName());
  }
}
