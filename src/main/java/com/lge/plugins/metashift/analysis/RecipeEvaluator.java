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

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.MetricDataSummary;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeEvaluationSummary;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RecipeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluator implements Collector<Recipes, Void> {

  private static final String OVERALL = "__PROJECT__";

  private final Configuration configuration;
  private List<String> keys;
  private final Map<String, LinesOfCode> linesOfCode;
  private final Map<String, MetricDataSummary> premirrorCache;
  private final Map<String, MetricDataSummary> sharedStateCache;
  private final Map<String, MetricDataSummary> recipeViolations;
  private final Map<String, MetricDataSummary> comments;
  private final Map<String, MetricDataSummary> codeViolations;
  private final Map<String, MetricDataSummary> complexity;
  private final Map<String, MetricDataSummary> duplications;
  private final Map<String, MetricDataSummary> unitTests;
  private final Map<String, MetricDataSummary> statementCoverage;
  private final Map<String, MetricDataSummary> branchCoverage;
  private final Map<String, MetricDataSummary> mutationTests;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public RecipeEvaluator(Configuration configuration) {
    this.configuration = configuration;
    keys = new ArrayList<>();
    linesOfCode = new HashMap<>();
    premirrorCache = new HashMap<>();
    sharedStateCache = new HashMap<>();
    recipeViolations = new HashMap<>();
    comments = new HashMap<>();
    codeViolations = new HashMap<>();
    complexity = new HashMap<>();
    duplications = new HashMap<>();
    unitTests = new HashMap<>();
    statementCoverage = new HashMap<>();
    branchCoverage = new HashMap<>();
    mutationTests = new HashMap<>();
  }

  /**
   * Returns the list of recipe names.
   *
   * @return list of recipe names
   */
  public List<String> getKeys() {
    return keys;
  }

  /**
   * Adds the lines of code data.
   *
   * @param recipes to parse
   */
  private void addLinesOfCodeData(Recipes recipes) {
    linesOfCode.put(OVERALL, new LinesOfCodeCollector().parse(recipes));
    recipes.forEach(o -> linesOfCode.put(o.getRecipe(), new LinesOfCodeCollector().parse(o)));
  }

  /**
   * Returns the lines of code.
   *
   * @param recipe name
   * @return lines of code
   */
  public LinesOfCode getLinesOfCode(String recipe) {
    return linesOfCode.getOrDefault(recipe, new LinesOfCode());
  }

  /**
   * Returns the lines of code.
   *
   * @return lines of code
   */
  public LinesOfCode getLinesOfCode() {
    return getLinesOfCode(OVERALL);
  }

  /**
   * Returns new MetricDataSummary object.
   *
   * @param evaluation   data
   * @param statistics   data
   * @param distribution data
   * @return metric data summary
   */
  private MetricDataSummary of(Evaluation evaluation, Statistics statistics,
      Distribution distribution) {
    return new MetricDataSummary(evaluation, statistics, distribution);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addPremirrorCache(Recipes recipes) {
    Evaluation evaluation = new PremirrorCacheEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new PremirrorCacheStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new PremirrorCacheDistributionCollector().parse(recipes);
    premirrorCache.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new PremirrorCacheEvaluationCollector(configuration).parse(recipe);
      distribution = new PremirrorCacheDistributionCollector().parse(recipe);
      premirrorCache.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getPremirrorCache(String recipe) {
    return premirrorCache.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getPremirrorCache() {
    return getPremirrorCache(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addSharedStateCache(Recipes recipes) {
    Evaluation evaluation = new SharedStateCacheEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new SharedStateCacheStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new SharedStateCacheDistributionCollector().parse(recipes);
    sharedStateCache.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new SharedStateCacheEvaluationCollector(configuration).parse(recipe);
      distribution = new SharedStateCacheDistributionCollector().parse(recipe);
      sharedStateCache.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getSharedStateCache(String recipe) {
    return sharedStateCache.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getSharedStateCache() {
    return getSharedStateCache(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addRecipeViolations(Recipes recipes) {
    Evaluation evaluation = new RecipeViolationEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new RecipeViolationStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new RecipeViolationDistributionCollector().parse(recipes);
    recipeViolations.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new RecipeViolationEvaluationCollector(configuration).parse(recipe);
      distribution = new RecipeViolationDistributionCollector().parse(recipe);
      recipeViolations.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getRecipeViolations(String recipe) {
    return recipeViolations.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getRecipeViolations() {
    return getRecipeViolations(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addComments(Recipes recipes) {
    Evaluation evaluation = new CommentEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new CommentStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new CommentDistributionCollector().parse(recipes);
    comments.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new CommentEvaluationCollector(configuration).parse(recipe);
      distribution = new CommentDistributionCollector().parse(recipe);
      comments.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getComments(String recipe) {
    return comments.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getComments() {
    return comments.get(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addCodeViolations(Recipes recipes) {
    Evaluation evaluation = new CodeViolationEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new CodeViolationStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new CodeViolationDistributionCollector().parse(recipes);
    codeViolations.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new CodeViolationEvaluationCollector(configuration).parse(recipe);
      distribution = new CodeViolationDistributionCollector().parse(recipe);
      codeViolations.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getCodeViolations(String recipe) {
    return codeViolations.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getCodeViolations() {
    return getCodeViolations(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addComplexity(Recipes recipes) {
    Evaluation evaluation = new ComplexityEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new ComplexityStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new ComplexityDistributionCollector(configuration).parse(recipes);
    complexity.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new ComplexityEvaluationCollector(configuration).parse(recipe);
      distribution = new ComplexityDistributionCollector(configuration).parse(recipe);
      complexity.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getComplexity(String recipe) {
    return complexity.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getComplexity() {
    return getComplexity(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addDuplications(Recipes recipes) {
    Evaluation evaluation = new DuplicationEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new DuplicationStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new DuplicationDistributionCollector(configuration).parse(recipes);
    duplications.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new DuplicationEvaluationCollector(configuration).parse(recipe);
      distribution = new DuplicationDistributionCollector(configuration).parse(recipe);
      duplications.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getDuplications(String recipe) {
    return duplications.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getDuplications() {
    return getDuplications(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addUnitTests(Recipes recipes) {
    Evaluation evaluation = new TestEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new TestStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new TestDistributionCollector().parse(recipes);
    unitTests.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new TestEvaluationCollector(configuration).parse(recipe);
      distribution = new TestDistributionCollector().parse(recipe);
      unitTests.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getUnitTests(String recipe) {
    return unitTests.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getUnitTests() {
    return getUnitTests(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addStatementCoverage(Recipes recipes) {
    Evaluation evaluation = new StatementCoverageEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new StatementCoverageStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new StatementCoverageDistributionCollector().parse(recipes);
    statementCoverage.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new StatementCoverageEvaluationCollector(configuration).parse(recipe);
      distribution = new StatementCoverageDistributionCollector().parse(recipe);
      statementCoverage.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getStatementCoverage(String recipe) {
    return statementCoverage.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getStatementCoverage() {
    return getStatementCoverage(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addBranchCoverage(Recipes recipes) {
    Evaluation evaluation = new BranchCoverageEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new BranchCoverageStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new BranchCoverageDistributionCollector().parse(recipes);
    branchCoverage.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new BranchCoverageEvaluationCollector(configuration).parse(recipe);
      distribution = new BranchCoverageDistributionCollector().parse(recipe);
      branchCoverage.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getBranchCoverage(String recipe) {
    return branchCoverage.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getBranchCoverage() {
    return getBranchCoverage(OVERALL);
  }

  /**
   * Adds the metric data summaries.
   *
   * @param recipes to parse
   */
  private void addMutationTests(Recipes recipes) {
    Evaluation evaluation = new MutationTestEvaluationCollector(configuration).parse(recipes);
    Statistics statistics = new MutationTestStatisticsCollector(configuration).parse(recipes);
    Distribution distribution = new MutationTestDistributionCollector().parse(recipes);
    mutationTests.put(OVERALL, of(evaluation, statistics, distribution));

    for (Recipe recipe : recipes) {
      evaluation = new MutationTestEvaluationCollector(configuration).parse(recipe);
      distribution = new MutationTestDistributionCollector().parse(recipe);
      mutationTests.put(recipe.getRecipe(), of(evaluation, statistics, distribution));
    }
  }

  /**
   * Returns the metric data summary.
   *
   * @param recipe name
   * @return metric data summary
   */
  public MetricDataSummary getMutationTests(String recipe) {
    return mutationTests.getOrDefault(recipe, new MetricDataSummary());
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getMutationTests() {
    return getMutationTests(OVERALL);
  }

  /**
   * Returns the recipe evaluation summary.
   *
   * @param recipe name
   * @return recipe evaluation summary
   */
  public RecipeEvaluationSummary getEvaluationSummary(String recipe) {
    return new RecipeEvaluationSummary(
        linesOfCode.getOrDefault(recipe, new LinesOfCode()),
        premirrorCache.getOrDefault(recipe, new MetricDataSummary()),
        sharedStateCache.getOrDefault(recipe, new MetricDataSummary()),
        recipeViolations.getOrDefault(recipe, new MetricDataSummary()),
        comments.getOrDefault(recipe, new MetricDataSummary()),
        codeViolations.getOrDefault(recipe, new MetricDataSummary()),
        complexity.getOrDefault(recipe, new MetricDataSummary()),
        duplications.getOrDefault(recipe, new MetricDataSummary()),
        unitTests.getOrDefault(recipe, new MetricDataSummary()),
        statementCoverage.getOrDefault(recipe, new MetricDataSummary()),
        branchCoverage.getOrDefault(recipe, new MetricDataSummary()),
        mutationTests.getOrDefault(recipe, new MetricDataSummary())
    );
  }

  /**
   * Returns the recipe evaluation summary.
   *
   * @return recipe evaluation summary
   */
  public RecipeEvaluationSummary getEvaluationSummary() {
    return getEvaluationSummary(OVERALL);
  }

  @Override
  public Void parse(Recipes s) {
    keys = s.stream().map(Recipe::getRecipe).collect(Collectors.toList());
    addLinesOfCodeData(s);
    addPremirrorCache(s);
    addSharedStateCache(s);
    addRecipeViolations(s);
    addComments(s);
    addCodeViolations(s);
    addComplexity(s);
    addDuplications(s);
    addUnitTests(s);
    addStatementCoverage(s);
    addBranchCoverage(s);
    addMutationTests(s);
    return null;
  }
}
