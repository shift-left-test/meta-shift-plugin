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

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Jenkins configuration for the plugin.
 *
 * @author Sung Gon Kim
 */
public class Configuration implements Criteria, BuildStatus {

  /**
   * Represents the threshold for the premirror cache availability.
   */
  private int premirrorCacheThreshold;

  /**
   * Represents the threshold for the shared state cache availability.
   */
  private int sharedStateCacheThreshold;

  /**
   * Represents the threshold for the code violations.
   */
  private double codeViolationThreshold;

  /**
   * Represents the threshold for the comments.
   */
  private int commentThreshold;

  /**
   * Represents the tolerance of the complexity.
   */
  private long complexityTolerance;

  /**
   * Represents the threshold for the complexity.
   */
  private int complexityThreshold;

  /**
   * Represents the threshold for the statement coverage.
   */
  private int statementCoverageThreshold;

  /**
   * Represents the threshold for the branch coverage.
   */
  private int branchCoverageThreshold;

  /**
   * Represents the duplication tolerance.
   */
  private long duplicationTolerance;

  /**
   * Represents the threshold for the duplications.
   */
  private int duplicationThreshold;

  /**
   * Represents the threshold for the mutation test.
   */
  private int mutationTestThreshold;

  /**
   * Represents the threshold for the recipe violations.
   */
  private double recipeViolationThreshold;

  /**
   * Represents the threshold for the test.
   */
  private int testThreshold;

  /**
   * Represents whether the unsatisfied premirror cache sets the build status unstable.
   */
  private boolean premirrorCacheAsUnstable;

  /**
   * Represents whether the unsatisfied shared state cache sets the build status unstable.
   */
  private boolean sharedStateCacheAsUnstable;

  /**
   * Represents whether the unsatisfied code violation sets the build status unstable.
   */
  private boolean codeViolationsAsUnstable;

  /**
   * Represents whether the unsatisfied comment sets the build status unstable.
   */
  private boolean commentsAsUnstable;

  /**
   * Represents whether the unsatisfied premirror cache sets the build status unstable.
   */
  private boolean complexityAsUnstable;

  /**
   * Represents whether the unsatisfied statement coverage sets the build status unstable.
   */
  private boolean statementCoverageAsUnstable;

  /**
   * Represents whether the unsatisfied branch coverage sets the build status unstable.
   */
  private boolean branchCoverageAsUnstable;

  /**
   * Represents whether the unsatisfied duplication sets the build status unstable.
   */
  private boolean duplicationsAsUnstable;

  /**
   * Represents whether the unsatisfied mutation test sets the build status unstable.
   */
  private boolean mutationTestAsUnstable;

  /**
   * Represents whether the unsatisfied recipe violation sets the build status unstable.
   */
  private boolean recipeViolationsAsUnstable;

  /**
   * Represents whether the unsatisfied test sets the build status unstable.
   */
  private boolean testAsUnstable;

  /**
   * Default constructor.
   */
  public Configuration() {
    this(80, 80, 0.1, 20, 0.1, 10, 10, 10, 10, 95, 80, 40, 85,
        false, false, false, false, false, false, false, false, false, false, false);
  }

  /**
   * Default constructor.
   *
   * @param premirrorCacheThreshold    threshold
   * @param sharedStateCacheThreshold  threshold
   * @param recipeViolationThreshold   threshold
   * @param commentThreshold           threshold
   * @param codeViolationThreshold     threshold
   * @param complexityTolerance        complexity tolerance
   * @param complexityThreshold        threshold
   * @param duplicationTolerance       duplication tolerance
   * @param duplicationThreshold       threshold
   * @param testThreshold              threshold
   * @param statementCoverageThreshold threshold
   * @param branchCoverageThreshold    threshold
   * @param mutationTestThreshold      threshold
   * @param premirrorCacheAsUnstable   marks a build as unstable if unsatisfied
   * @param sharedStateCacheAsUnstable marks a build as unstable if unsatisfied
   * @param recipeViolationsAsUnstable marks a build as unstable if unsatisfied
   * @param commentsAsUnstable         marks a build as unstable if unsatisfied
   * @param codeViolationsAsUnstable   marks a build as unstable if unsatisfied
   * @param complexityAsUnstable       marks a build as unstable if unsatisfied
   * @param duplicationsAsUnstable     marks a build as unstable if unsatisfied
   * @param testAsUnstable             marks a build as unstable if unsatisfied
   * @param mutationTestAsUnstable     marks a build as unstable if unsatisfied
   */
  @DataBoundConstructor
  public Configuration(final int premirrorCacheThreshold,
      final int sharedStateCacheThreshold,
      final double recipeViolationThreshold,
      final int commentThreshold,
      final double codeViolationThreshold,
      final long complexityTolerance,
      final int complexityThreshold,
      final long duplicationTolerance,
      final int duplicationThreshold,
      final int testThreshold,
      final int statementCoverageThreshold,
      final int branchCoverageThreshold,
      final int mutationTestThreshold,
      final boolean premirrorCacheAsUnstable,
      final boolean sharedStateCacheAsUnstable,
      final boolean recipeViolationsAsUnstable,
      final boolean commentsAsUnstable,
      final boolean codeViolationsAsUnstable,
      final boolean complexityAsUnstable,
      final boolean duplicationsAsUnstable,
      final boolean testAsUnstable,
      final boolean statementCoverageAsUnstable,
      final boolean branchCoverageAsUnstable,
      final boolean mutationTestAsUnstable) {
    this.premirrorCacheThreshold = premirrorCacheThreshold;
    this.sharedStateCacheThreshold = sharedStateCacheThreshold;
    this.recipeViolationThreshold = recipeViolationThreshold;
    this.commentThreshold = commentThreshold;
    this.codeViolationThreshold = codeViolationThreshold;
    this.complexityTolerance = complexityTolerance;
    this.complexityThreshold = complexityThreshold;
    this.duplicationTolerance = duplicationTolerance;
    this.duplicationThreshold = duplicationThreshold;
    this.testThreshold = testThreshold;
    this.statementCoverageThreshold = statementCoverageThreshold;
    this.branchCoverageThreshold = branchCoverageThreshold;
    this.mutationTestThreshold = mutationTestThreshold;
    this.premirrorCacheAsUnstable = premirrorCacheAsUnstable;
    this.sharedStateCacheAsUnstable = sharedStateCacheAsUnstable;
    this.recipeViolationsAsUnstable = recipeViolationsAsUnstable;
    this.commentsAsUnstable = commentsAsUnstable;
    this.codeViolationsAsUnstable = codeViolationsAsUnstable;
    this.complexityAsUnstable = complexityAsUnstable;
    this.duplicationsAsUnstable = duplicationsAsUnstable;
    this.testAsUnstable = testAsUnstable;
    this.statementCoverageAsUnstable = statementCoverageAsUnstable;
    this.branchCoverageAsUnstable = branchCoverageAsUnstable;
    this.mutationTestAsUnstable = mutationTestAsUnstable;

    this.sanitizeValues();
  }

  @Override
  public int getPremirrorCacheThreshold() {
    return premirrorCacheThreshold;
  }

  @Override
  public void setPremirrorCacheThreshold(int threshold) {
    premirrorCacheThreshold = threshold;
  }

  @Override
  public int getSharedStateCacheThreshold() {
    return sharedStateCacheThreshold;
  }

  @Override
  public void setSharedStateCacheThreshold(int threshold) {
    sharedStateCacheThreshold = threshold;
  }

  @Override
  public double getCodeViolationThreshold() {
    return codeViolationThreshold;
  }

  @Override
  public void setCodeViolationThreshold(double threshold) {
    codeViolationThreshold = threshold;
  }

  @Override
  public int getCommentThreshold() {
    return commentThreshold;
  }

  @Override
  public void setCommentThreshold(int threshold) {
    commentThreshold = threshold;
  }

  @Override
  public long getComplexityTolerance() {
    return complexityTolerance;
  }

  @Override
  public void setComplexityTolerance(long tolerance) {
    complexityTolerance = tolerance;
  }

  @Override
  public int getComplexityThreshold() {
    return complexityThreshold;
  }

  @Override
  public void setComplexityThreshold(int threshold) {
    complexityThreshold = threshold;
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
  public long getDuplicationTolerance() {
    return duplicationTolerance;
  }

  @Override
  public void setDuplicationTolerance(long tolerance) {
    this.duplicationTolerance = tolerance;
  }

  @Override
  public int getDuplicationThreshold() {
    return duplicationThreshold;
  }

  @Override
  public void setDuplicationThreshold(int threshold) {
    duplicationThreshold = threshold;
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
  public double getRecipeViolationThreshold() {
    return recipeViolationThreshold;
  }

  @Override
  public void setRecipeViolationThreshold(double threshold) {
    recipeViolationThreshold = threshold;
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
  public boolean isPremirrorCacheAsUnstable() {
    return premirrorCacheAsUnstable;
  }

  @Override
  public void setPremirrorCacheAsUnstable(boolean allowed) {
    premirrorCacheAsUnstable = allowed;
  }

  @Override
  public boolean isSharedStateCacheAsUnstable() {
    return sharedStateCacheAsUnstable;
  }

  @Override
  public void setSharedStateCacheAsUnstable(boolean allowed) {
    sharedStateCacheAsUnstable = allowed;
  }

  @Override
  public boolean isCodeViolationsAsUnstable() {
    return codeViolationsAsUnstable;
  }

  @Override
  public void setCodeViolationsAsUnstable(boolean allowed) {
    codeViolationsAsUnstable = allowed;
  }

  @Override
  public boolean isCommentsAsUnstable() {
    return commentsAsUnstable;
  }

  @Override
  public void setCommentsAsUnstable(boolean allowed) {
    commentsAsUnstable = allowed;
  }

  @Override
  public boolean isComplexityAsUnstable() {
    return complexityAsUnstable;
  }

  @Override
  public void setComplexityAsUnstable(boolean allowed) {
    complexityAsUnstable = allowed;
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
  public boolean isDuplicationsAsUnstable() {
    return duplicationsAsUnstable;
  }

  @Override
  public void setDuplicationsAsUnstable(boolean allowed) {
    duplicationsAsUnstable = allowed;
  }

  @Override
  public boolean isMutationTestAsUnstable() {
    return mutationTestAsUnstable;
  }

  @Override
  public void setMutationTestAsUnstable(boolean allowed) {
    mutationTestAsUnstable = allowed;
  }

  @Override
  public boolean isRecipeViolationsAsUnstable() {
    return recipeViolationsAsUnstable;
  }

  @Override
  public void setRecipeViolationsAsUnstable(boolean allowed) {
    recipeViolationsAsUnstable = allowed;
  }

  @Override
  public boolean isTestAsUnstable() {
    return testAsUnstable;
  }

  @Override
  public void setTestAsUnstable(boolean allowed) {
    testAsUnstable = allowed;
  }

  /**
   * Sanitizes the input values.
   */
  public void sanitizeValues() {
    premirrorCacheThreshold = Math.max(0, Math.min(100, premirrorCacheThreshold));
    sharedStateCacheThreshold = Math.max(0, Math.min(100, sharedStateCacheThreshold));
    recipeViolationThreshold = Math.max(0.0, recipeViolationThreshold);
    commentThreshold = Math.max(0, Math.min(100, commentThreshold));
    codeViolationThreshold = Math.max(0.0, codeViolationThreshold);
    complexityTolerance = Math.max(0, complexityTolerance);
    complexityThreshold = Math.max(0, Math.min(100, complexityThreshold));
    duplicationTolerance = Math.max(0, duplicationTolerance);
    duplicationThreshold = Math.max(0, Math.min(100, duplicationThreshold));
    testThreshold = Math.max(0, Math.min(100, testThreshold));
    statementCoverageThreshold = Math.max(0, Math.min(100, statementCoverageThreshold));
    branchCoverageThreshold = Math.max(0, Math.min(100, branchCoverageThreshold));
    mutationTestThreshold = Math.max(0, Math.min(100, mutationTestThreshold));
  }
}
