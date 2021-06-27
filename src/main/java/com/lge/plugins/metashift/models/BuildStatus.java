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
 * An interface which represents the build status set up.
 *
 * @author Sung Gon Kim
 */
public interface BuildStatus {

  /**
   * Shows that the unsatisfied premirror cache metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isPremirrorCacheAsUnstable();

  /**
   * Sets whether the unsatisfied premirror cache metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setPremirrorCacheAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied shared state state cache metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isSharedStateCacheAsUnstable();

  /**
   * Sets whether the unsatisfied shared state cache metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setSharedStateCacheAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied code violation metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isCodeViolationsAsUnstable();

  /**
   * Sets whether the unsatisfied code violation metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setCodeViolationsAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied comment metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isCommentsAsUnstable();

  /**
   * Sets whether the unsatisfied comment metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setCommentsAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied complexity metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isComplexityAsUnstable();

  /**
   * Sets whether the unsatisfied complexity metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setComplexityAsUnstable(boolean allowed);

  /**
   * Shows that the unsatisfied coverage metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isCoverageAsUnstable();

  /**
   * Sets whether the unsatisfied coverage metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setCoverageAsUnstable(boolean allowed);

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
   * Shows that the unsatisfied duplication metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isDuplicationsAsUnstable();

  /**
   * Sets whether the unsatisfied duplication metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setDuplicationsAsUnstable(boolean allowed);

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

  /**
   * Shows that the unsatisfied recipe violation metric marks the build status unstable.
   *
   * @return true if the metric sets the build status unstable, false otherwise
   */
  boolean isRecipeViolationsAsUnstable();

  /**
   * Sets whether the unsatisfied recipe violation metric marks the build status unstable.
   *
   * @param allowed to set the build status
   */
  void setRecipeViolationsAsUnstable(boolean allowed);

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
}
