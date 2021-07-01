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
   * Returns the complexity level.
   *
   * @return complexity level
   */
  long getComplexityLevel();

  /**
   * Sets the complexity level.
   *
   * @param level fot the complexity
   */
  void setComplexityLevel(long level);

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
