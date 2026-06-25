/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Jenkins configuration for the plugin.
 *
 * @author Sung Gon Kim
 */
public class Configuration implements Criteria, BuildStatus {

  /**
   * Represents the threshold for the test.
   */
  private int testThreshold;

  /**
   * Represents the threshold for the statement coverage.
   */
  private int statementCoverageThreshold;

  /**
   * Represents the threshold for the branch coverage.
   */
  private int branchCoverageThreshold;

  /**
   * Represents the threshold for the mutation test.
   */
  private int mutationTestThreshold;

  /**
   * Represents whether the unsatisfied test sets the build status unstable.
   */
  private boolean testAsUnstable;

  /**
   * Represents whether the unsatisfied statement coverage sets the build status unstable.
   */
  private boolean statementCoverageAsUnstable;

  /**
   * Represents whether the unsatisfied branch coverage sets the build status unstable.
   */
  private boolean branchCoverageAsUnstable;

  /**
   * Represents whether the unsatisfied mutation test sets the build status unstable.
   */
  private boolean mutationTestAsUnstable;

  /**
   * Default constructor.
   */
  public Configuration() {
    this(95, 80, 40, 85, false, false, false, false);
  }

  /**
   * Default constructor.
   *
   * @param testThreshold              threshold
   * @param statementCoverageThreshold threshold
   * @param branchCoverageThreshold    threshold
   * @param mutationTestThreshold      threshold
   * @param testAsUnstable             marks a build as unstable if unsatisfied
   * @param statementCoverageAsUnstable marks a build as unstable if unsatisfied
   * @param branchCoverageAsUnstable   marks a build as unstable if unsatisfied
   * @param mutationTestAsUnstable     marks a build as unstable if unsatisfied
   */
  @DataBoundConstructor
  public Configuration(final int testThreshold,
      final int statementCoverageThreshold,
      final int branchCoverageThreshold,
      final int mutationTestThreshold,
      final boolean testAsUnstable,
      final boolean statementCoverageAsUnstable,
      final boolean branchCoverageAsUnstable,
      final boolean mutationTestAsUnstable) {
    this.testThreshold = testThreshold;
    this.statementCoverageThreshold = statementCoverageThreshold;
    this.branchCoverageThreshold = branchCoverageThreshold;
    this.mutationTestThreshold = mutationTestThreshold;
    this.testAsUnstable = testAsUnstable;
    this.statementCoverageAsUnstable = statementCoverageAsUnstable;
    this.branchCoverageAsUnstable = branchCoverageAsUnstable;
    this.mutationTestAsUnstable = mutationTestAsUnstable;

    this.sanitizeValues();
  }

  @Override
  public int getTestThreshold() {
    return testThreshold;
  }

  @Override
  public void setTestThreshold(int threshold) {
    testThreshold = threshold;
  }

  @Override
  public int getStatementCoverageThreshold() {
    return statementCoverageThreshold;
  }

  @Override
  public void setStatementCoverageThreshold(int threshold) {
    statementCoverageThreshold = threshold;
  }

  @Override
  public int getBranchCoverageThreshold() {
    return branchCoverageThreshold;
  }

  @Override
  public void setBranchCoverageThreshold(int threshold) {
    branchCoverageThreshold = threshold;
  }

  @Override
  public int getMutationTestThreshold() {
    return mutationTestThreshold;
  }

  @Override
  public void setMutationTestThreshold(int threshold) {
    mutationTestThreshold = threshold;
  }

  @Override
  public boolean isTestAsUnstable() {
    return testAsUnstable;
  }

  @Override
  public void setTestAsUnstable(boolean allowed) {
    testAsUnstable = allowed;
  }

  @Override
  public boolean isStatementCoverageAsUnstable() {
    return statementCoverageAsUnstable;
  }

  @Override
  public void setStatementCoverageAsUnstable(boolean allowed) {
    statementCoverageAsUnstable = allowed;
  }

  @Override
  public boolean isBranchCoverageAsUnstable() {
    return branchCoverageAsUnstable;
  }

  @Override
  public void setBranchCoverageAsUnstable(boolean allowed) {
    branchCoverageAsUnstable = allowed;
  }

  @Override
  public boolean isMutationTestAsUnstable() {
    return mutationTestAsUnstable;
  }

  @Override
  public void setMutationTestAsUnstable(boolean allowed) {
    mutationTestAsUnstable = allowed;
  }

  /**
   * Sanitizes the input values.
   */
  public void sanitizeValues() {
    testThreshold = Math.max(0, Math.min(100, testThreshold));
    statementCoverageThreshold = Math.max(0, Math.min(100, statementCoverageThreshold));
    branchCoverageThreshold = Math.max(0, Math.min(100, branchCoverageThreshold));
    mutationTestThreshold = Math.max(0, Math.min(100, mutationTestThreshold));
  }
}
