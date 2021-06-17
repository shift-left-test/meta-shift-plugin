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
 * Represents the Jenkins configuration.
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
   * Represents the level of the complexity.
   */
  private long complexityLevel;

  /**
   * Represents the threshold for the complexity.
   */
  private int complexityThreshold;

  /**
   * Represents the threshold for the coverage.
   */
  private int coverageThreshold;

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
   * Represents whether the unsatisfied coverage sets the build status unstable.
   */
  private boolean coverageAsUnstable;

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
    this(80, 80, 0.1, 30, 0.1, 4, 10, 10, 95, 60, 85,
        true, true, true, true, true, true, true, true, true, true);
  }

  /**
   * Default constructor.
   *
   * @param premirrorCacheThreshold    threshold
   * @param sharedStateCacheThreshold  threshold
   * @param recipeViolationThreshold   threshold
   * @param commentThreshold           threshold
   * @param codeViolationThreshold     threshold
   * @param complexityLevel            complexity level
   * @param complexityThreshold        threshold
   * @param duplicationThreshold       threshold
   * @param testThreshold              threshold
   * @param coverageThreshold          threshold
   * @param mutationTestThreshold      threshold
   * @param premirrorCacheAsUnstable   marks a build as unstable if unsatisfied
   * @param sharedStateCacheAsUnstable marks a build as unstable if unsatisfied
   * @param recipeViolationsAsUnstable marks a build as unstable if unsatisfied
   * @param commentsAsUnstable         marks a build as unstable if unsatisfied
   * @param codeViolationsAsUnstable   marks a build as unstable if unsatisfied
   * @param complexityAsUnstable       marks a build as unstable if unsatisfied
   * @param duplicationsAsUnstable     marks a build as unstable if unsatisfied
   * @param testAsUnstable             marks a build as unstable if unsatisfied
   * @param coverageAsUnstable         marks a build as unstable if unsatisfied
   * @param mutationTestAsUnstable     marks a build as unstable if unsatisfied
   */
  @DataBoundConstructor
  public Configuration(final int premirrorCacheThreshold,
      final int sharedStateCacheThreshold,
      final double recipeViolationThreshold,
      final int commentThreshold,
      final double codeViolationThreshold,
      final long complexityLevel,
      final int complexityThreshold,
      final int duplicationThreshold,
      final int testThreshold,
      final int coverageThreshold,
      final int mutationTestThreshold,
      final boolean premirrorCacheAsUnstable,
      final boolean sharedStateCacheAsUnstable,
      final boolean recipeViolationsAsUnstable,
      final boolean commentsAsUnstable,
      final boolean codeViolationsAsUnstable,
      final boolean complexityAsUnstable,
      final boolean duplicationsAsUnstable,
      final boolean testAsUnstable,
      final boolean coverageAsUnstable,
      final boolean mutationTestAsUnstable) {
    this.premirrorCacheThreshold = premirrorCacheThreshold;
    this.sharedStateCacheThreshold = sharedStateCacheThreshold;
    this.recipeViolationThreshold = recipeViolationThreshold;
    this.commentThreshold = commentThreshold;
    this.codeViolationThreshold = codeViolationThreshold;
    this.complexityLevel = complexityLevel;
    this.complexityThreshold = complexityThreshold;
    this.duplicationThreshold = duplicationThreshold;
    this.testThreshold = testThreshold;
    this.coverageThreshold = coverageThreshold;
    this.mutationTestThreshold = mutationTestThreshold;
    this.premirrorCacheAsUnstable = premirrorCacheAsUnstable;
    this.sharedStateCacheAsUnstable = sharedStateCacheAsUnstable;
    this.recipeViolationsAsUnstable = recipeViolationsAsUnstable;
    this.commentsAsUnstable = commentsAsUnstable;
    this.codeViolationsAsUnstable = codeViolationsAsUnstable;
    this.complexityAsUnstable = complexityAsUnstable;
    this.duplicationsAsUnstable = duplicationsAsUnstable;
    this.testAsUnstable = testAsUnstable;
    this.coverageAsUnstable = coverageAsUnstable;
    this.mutationTestAsUnstable = mutationTestAsUnstable;
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
  public long getComplexityLevel() {
    return complexityLevel;
  }

  @Override
  public void setComplexityLevel(long level) {
    complexityLevel = level;
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
  public int getCoverageThreshold() {
    return coverageThreshold;
  }

  @Override
  public void setCoverageThreshold(int threshold) {
    coverageThreshold = threshold;
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
  public boolean isCoverageAsUnstable() {
    return coverageAsUnstable;
  }

  @Override
  public void setCoverageAsUnstable(boolean allowed) {
    coverageAsUnstable = allowed;
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
}