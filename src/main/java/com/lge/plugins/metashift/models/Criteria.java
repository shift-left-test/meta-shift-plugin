/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the quality metric criteria.
 *
 * @author Sung Gon Kim
 */
public interface Criteria {

  /**
   * Returns the test threshold.
   *
   * @return test threshold
   */
  int getTestThreshold();

  /**
   * Sets the test threshold.
   *
   * @param threshold for the test
   */
  void setTestThreshold(int threshold);

  /**
   * Returns the statement coverage threshold.
   *
   * @return statement coverage threshold
   */
  int getStatementCoverageThreshold();

  /**
   * Sets the statement coverage threshold.
   *
   * @param threshold for the statement coverage
   */
  void setStatementCoverageThreshold(int threshold);

  /**
   * Returns the branch coverage threshold.
   *
   * @return branch coverage threshold
   */
  int getBranchCoverageThreshold();

  /**
   * Sets the branch coverage threshold.
   *
   * @param threshold for the branch coverage
   */
  void setBranchCoverageThreshold(int threshold);

  /**
   * Returns the mutation test threshold.
   *
   * @return mutation test threshold.
   */
  int getMutationTestThreshold();

  /**
   * Sets the mutation test threshold.
   *
   * @param threshold for the mutation test
   */
  void setMutationTestThreshold(int threshold);
}
