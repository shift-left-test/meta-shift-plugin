/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Aggregate;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Recipes;
import hudson.model.Result;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * BuildStatusResolver class.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusResolver implements Aggregate<Result>, Collector<Recipes, Void> {

  private final Configuration configuration;
  private final Map<Type, Result> status;

  /**
   * Default constructor.
   *
   * @param configuration for build status
   */
  public BuildStatusResolver(Configuration configuration) {
    this.configuration = configuration;
    status = new EnumMap<>(Aggregate.Type.class);
    Stream.of(Type.values()).forEach(o -> status.put(o, Result.SUCCESS));
  }

  /**
   * Returns the result object based on the inputs.
   *
   * @param unstable   status
   * @param evaluation object
   * @return result object
   */
  private Result toResult(boolean unstable, Evaluation evaluation) {
    boolean available = evaluation.isAvailable();
    boolean qualified = evaluation.isQualified();
    return !unstable || !available || qualified ? Result.SUCCESS : Result.UNSTABLE;
  }

  @Override
  public Result getUnitTests() {
    return status.get(Type.UNIT_TESTS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setUnitTests(Recipes recipes) {
    status.put(Type.UNIT_TESTS,
        toResult(configuration.isTestAsUnstable(),
            new UnitTestEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getStatementCoverage() {
    return status.get(Type.STATEMENT_COVERAGE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setStatementCoverage(Recipes recipes) {
    status.put(Type.STATEMENT_COVERAGE,
        toResult(configuration.isStatementCoverageAsUnstable(),
            new StatementCoverageEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getBranchCoverage() {
    return status.get(Type.BRANCH_COVERAGE);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setBranchCoverage(Recipes recipes) {
    status.put(Type.BRANCH_COVERAGE,
        toResult(configuration.isBranchCoverageAsUnstable(),
            new BranchCoverageEvaluator(configuration).parse(recipes)));
  }

  @Override
  public Result getMutationTests() {
    return status.get(Type.MUTATION_TESTS);
  }

  /**
   * Sets the build status.
   *
   * @param recipes to parses
   */
  private void setMutationTests(Recipes recipes) {
    status.put(Type.MUTATION_TESTS,
        toResult(configuration.isMutationTestAsUnstable(),
            new MutationTestEvaluator(configuration).parse(recipes)));
  }

  /**
   * Returns the combined build status.
   *
   * @return build status
   */
  public Result getCombined() {
    return status.values().stream().reduce(Result.SUCCESS, (a, b) -> a.combine(b));
  }

  @Override
  public Void parse(Recipes recipes) {
    setUnitTests(recipes);
    setStatementCoverage(recipes);
    setBranchCoverage(recipes);
    setMutationTests(recipes);
    return null;
  }
}
