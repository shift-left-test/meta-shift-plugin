/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.jsoniter.any.Any;
import com.lge.plugins.metashift.aggregators.BranchCoverageDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.CodeViolationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.CommentDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.ComplexityDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.DuplicationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.MutationTestDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.PremirrorCacheDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.RecipeAggregator;
import com.lge.plugins.metashift.aggregators.RecipeViolationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.SharedStateCacheDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.StatementCoverageDataSummaryAggregator;
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
import com.lge.plugins.metashift.analysis.RecipeViolationCounter;
import com.lge.plugins.metashift.analysis.RecipeViolationEvaluator;
import com.lge.plugins.metashift.analysis.SharedStateCacheCounter;
import com.lge.plugins.metashift.analysis.SharedStateCacheEvaluator;
import com.lge.plugins.metashift.analysis.StatementCoverageCounter;
import com.lge.plugins.metashift.analysis.StatementCoverageEvaluator;
import com.lge.plugins.metashift.analysis.UnitTestCounter;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.ViolationData;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils.Function;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

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

  private Void addLinesOfCode(Recipe recipe) throws IOException {
    LinesOfCode linesOfCode = new LinesOfCodeCollector().parse(recipe);
    put(Metric.NONE, Data.LINES_OF_CODE, recipe.getName(), JSONObject.fromObject(linesOfCode));
    return null;
  }

  private FilePath getFilePath(FilePath base, String prefix, String file) {
    if (new File(file).isAbsolute()) {
      return new FilePath(base.getChannel(), file);
    } else {
      return new FilePath(new FilePath(base.getChannel(), prefix), file);
    }
  }

  private Void writeFile(String recipe, String file, String basedir)
      throws IOException, InterruptedException {
    if (dataSource.has(Scope.RECIPE.name(), Metric.NONE.name(), Data.FILE.name(), recipe, file)) {
      return null;
    }
    Optional<FilePath> directory = path.listDirectories().stream()
        .filter(o -> o.getName().startsWith(recipe)).findFirst();
    if (!directory.isPresent() || !directory.get().isDirectory()) {
      return null;
    }
    try {
      Any metadata = JsonUtils.createObject(directory.get().child("metadata.json"));
      FilePath filePath = getFilePath(path, metadata.toString(basedir), file);
      String data = IOUtils.toString(filePath.read(), StandardCharsets.UTF_8);
      put(Metric.NONE, Data.FILE, recipe, file, data);
    } catch (NoSuchFileException ignored) {
      // ignored
    }
    return null;
  }

  private <T> Void addObjects(Metric metric, String recipe, Map<String, List<T>> group)
      throws IOException, InterruptedException {
    for (Entry<String, List<T>> entry : group.entrySet()) {
      put(metric, Data.OBJECTS, recipe, entry.getKey(), JSONArray.fromObject(entry.getValue()));
      String basedir = (metric == Metric.RECIPE_VIOLATIONS) ? "PWD" : "S";
      writeFile(recipe, entry.getKey(), basedir);
    }
    return null;
  }

  private Void addPremirrorCache(Recipe recipe) throws IOException {
    add(Metric.PREMIRROR_CACHE,
        new PremirrorCacheEvaluator(configuration),
        new PremirrorCacheCounter(),
        new PremirrorCacheDataSummaryAggregator(configuration),
        recipe);
    return null;
  }

  private Void addSharedStateCache(Recipe recipe) throws IOException {
    add(Metric.SHARED_STATE_CACHE,
        new SharedStateCacheEvaluator(configuration),
        new SharedStateCacheCounter(),
        new SharedStateCacheDataSummaryAggregator(configuration),
        recipe);
    return null;
  }

  private Void addRecipeViolations(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.RECIPE_VIOLATIONS,
        new RecipeViolationEvaluator(configuration),
        new RecipeViolationCounter(),
        new RecipeViolationDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<ViolationData>> group = recipe.objects(RecipeViolationData.class)
        .collect(Collectors.groupingBy(ViolationData::getFile));
    addObjects(Metric.RECIPE_VIOLATIONS, recipe.getName(), group);
    return null;
  }

  private Void addComments(Recipe recipe) throws IOException {
    add(Metric.COMMENTS,
        new CommentEvaluator(configuration),
        new CommentCounter(),
        new CommentDataSummaryAggregator(configuration),
        recipe);
    return null;
  }

  private Void addCodeViolations(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.CODE_VIOLATIONS,
        new CodeViolationEvaluator(configuration),
        new CodeViolationCounter(),
        new CodeViolationDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<ViolationData>> group = recipe.objects(CodeViolationData.class)
        .collect(Collectors.groupingBy(ViolationData::getFile));
    addObjects(Metric.CODE_VIOLATIONS, recipe.getName(), group);
    return null;
  }

  private Void addComplexity(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.COMPLEXITY,
        new ComplexityEvaluator(configuration),
        new ComplexityCounter(configuration),
        new ComplexityDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<ComplexityData>> group = recipe.objects(ComplexityData.class)
        .filter(o -> o.getValue() >= configuration.getComplexityTolerance())
        .collect(Collectors.groupingBy(ComplexityData::getFile));
    addObjects(Metric.COMPLEXITY, recipe.getName(), group);
    return null;
  }

  private Void addDuplications(Recipe recipe) throws IOException, InterruptedException {
    add(Metric.DUPLICATIONS,
        new DuplicationEvaluator(configuration),
        new DuplicationCounter(configuration),
        new DuplicationDataSummaryAggregator(configuration),
        recipe);

    Map<String, List<DuplicationData>> group = recipe.objects(DuplicationData.class)
        .filter(o -> o.getDuplicatedLines() >= configuration.getDuplicationTolerance())
        .collect(Collectors.groupingBy(DuplicationData::getFile));
    addObjects(Metric.DUPLICATIONS, recipe.getName(), group);
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
        newTask(this::addLinesOfCode, recipe),
        newTask(this::addPremirrorCache, recipe),
        newTask(this::addSharedStateCache, recipe),
        newTask(this::addRecipeViolations, recipe),
        newTask(this::addComments, recipe),
        newTask(this::addCodeViolations, recipe),
        newTask(this::addComplexity, recipe),
        newTask(this::addDuplications, recipe),
        newTask(this::addUnitTests, recipe),
        newTask(this::addStatementCoverage, recipe),
        newTask(this::addBranchCoverage, recipe),
        newTask(this::addMutationTests, recipe)
    );
    return new RecipeReport(dataSource, recipe.getName());
  }
}
