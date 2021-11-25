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
   * Returns the premirror cache availability threshold.
   *
   * @return premirror cache threshold
   */
  int getPremirrorCacheThreshold();

  /**
   * Sets the premirror cache availability threshold.
   *
   * @param threshold for the premirror cache availability
   */
  void setPremirrorCacheThreshold(int threshold);

  /**
   * Returns the shared state cache availability threshold.
   *
   * @return shared state cache threshold
   */
  int getSharedStateCacheThreshold();

  /**
   * Sets the shared state cache availability threshold.
   *
   * @param threshold for the shared state cache availability
   */
  void setSharedStateCacheThreshold(int threshold);

  /**
   * Returns the code violation threshold.
   *
   * @return code violation threshold
   */
  double getCodeViolationThreshold();

  /**
   * Sets the code violation threshold.
   *
   * @param threshold for the code violations
   */
  void setCodeViolationThreshold(double threshold);

  /**
   * Returns the comment threshold.
   *
   * @return comment threshold
   */
  int getCommentThreshold();

  /**
   * Sets the comment threshold.
   *
   * @param threshold for the comments
   */
  void setCommentThreshold(int threshold);

  /**
   * Returns the complexity tolerance.
   *
   * @return complexity tolerance
   */
  int getComplexityTolerance();

  /**
   * Sets the complexity tolerance.
   *
   * @param tolerance fot the complexity
   */
  void setComplexityTolerance(int tolerance);

  /**
   * Returns the complexity threshold.
   *
   * @return complexity threshold
   */
  int getComplexityThreshold();

  /**
   * Sets the complexity threshold.
   *
   * @param threshold for the complexity
   */
  void setComplexityThreshold(int threshold);

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
   * Returns the minimum duplicate lines.
   *
   * @return minimum duplicate lines
   */
  int getDuplicationTolerance();

  /**
   * Sets the minimum duplicate lines.
   *
   * @param tolerance for duplications
   */
  void setDuplicationTolerance(int tolerance);

  /**
   * Returns the duplication threshold.
   *
   * @return duplication threshold
   */
  int getDuplicationThreshold();

  /**
   * Sets the duplication threshold.
   *
   * @param threshold for the duplications
   */
  void setDuplicationThreshold(int threshold);

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

  /**
   * Returns the recipe violation threshold.
   *
   * @return recipe violation threshold
   */
  double getRecipeViolationThreshold();

  /**
   * Sets the recipe violation threshold.
   *
   * @param threshold for the recipe violations
   */
  void setRecipeViolationThreshold(double threshold);

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
}
