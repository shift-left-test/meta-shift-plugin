/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * An interface which represents the build status set up.
 *
 * @author Sung Gon Kim
 */
public interface BuildStatus {

  /**
   * Shows that the unsatisfied test metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isTestAsUnstable();

  /**
   * Sets whether the unsatisfied test metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setTestAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied statement coverage metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isStatementCoverageAsUnstable();

  /**
   * Sets whether the unsatisfied statement coverage metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setStatementCoverageAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied branch coverage metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isBranchCoverageAsUnstable();

  /**
   * Sets whether the unsatisfied branch coverage metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setBranchCoverageAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied mutation test metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isMutationTestAsUnstable();

  /**
   * Sets whether the unsatisfied mutation test metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setMutationTestAsUnstable(boolean allowed);
}
