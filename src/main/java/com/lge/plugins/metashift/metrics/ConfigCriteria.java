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

package com.lge.plugins.metashift.metrics;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Config form of the quality metric criteria.
 */
public class ConfigCriteria {

  private int premirrorCacheThreshold;
  private boolean markUnstablePremirrorCache;

  private int sharedStateCacheThreshold;
  private boolean markUnstableSharedStateCache;

  private double codeViolationThreshold;
  private boolean markUnstableCodeViolation;

  private int commentThreshold;
  private boolean markUnstableComment;

  private long complexityLevel;
  private int complexityThreshold;
  private boolean markUnstableComplexity;

  private int coverageThreshold;
  private boolean markUnstableCoverage;

  private int duplicationThreshold;
  private boolean markUnstableDuplication;

  private int mutationTestThreshold;
  private boolean markUnstableMutationTest;

  private double recipeViolationThreshold;
  private boolean markUnstableRecipeViolation;

  private int testThreshold;
  private boolean markUnstableTest;

  /**
   * Default constructor.
   */
  public ConfigCriteria() {
    this(80, true, 80, true, 0.1, true,
        30, true, 0.1, true, 4, 10, true, 10, true,
        95, true, 60, true, 85, true);
  }

  /**
   * Default constructor.
   *
   * @param premirrorCacheThreshold   premirror cache threshold
   * @param sharedStateCacheThreshold shared state cache threshold
   * @param recipeViolationThreshold  recipe violation threshold
   * @param commentThreshold          comment threshold
   * @param codeViolationThreshold    code violation threshold
   * @param complexityLevel           complexity level
   * @param complexityThreshold       complexity threshold
   * @param duplicationThreshold      duplication threshold
   * @param testThreshold             test threshold
   * @param coverageThreshold         coverage threshold
   * @param mutationTestThreshold     mutation test threshold
   */
  @DataBoundConstructor
  public ConfigCriteria(final int premirrorCacheThreshold,
      final boolean markUnstablePremirrorCache,
      final int sharedStateCacheThreshold,
      final boolean markUnstableSharedStateCache,
      final double recipeViolationThreshold,
      final boolean markUnstableRecipeViolation,
      final int commentThreshold,
      final boolean markUnstableComment,
      final double codeViolationThreshold,
      final boolean markUnstableCodeViolation,
      final long complexityLevel,
      final int complexityThreshold,
      final boolean markUnstableComplexity,
      final int duplicationThreshold,
      final boolean markUnstableDuplication,
      final int testThreshold,
      final boolean markUnstableTest,
      final int coverageThreshold,
      final boolean markUnstableCoverage,
      final int mutationTestThreshold,
      final boolean markUnstableMutationTest) {
    this.premirrorCacheThreshold = premirrorCacheThreshold;
    this.markUnstablePremirrorCache = markUnstablePremirrorCache;

    this.sharedStateCacheThreshold = sharedStateCacheThreshold;
    this.markUnstableSharedStateCache = markUnstableSharedStateCache;

    this.recipeViolationThreshold = recipeViolationThreshold;
    this.markUnstableRecipeViolation = markUnstableRecipeViolation;

    this.commentThreshold = commentThreshold;
    this.markUnstableComment = markUnstableComment;

    this.codeViolationThreshold = codeViolationThreshold;
    this.markUnstableCodeViolation = markUnstableCodeViolation;

    this.complexityLevel = complexityLevel;
    this.complexityThreshold = complexityThreshold;
    this.markUnstableComplexity = markUnstableComplexity;

    this.duplicationThreshold = duplicationThreshold;
    this.markUnstableDuplication = markUnstableDuplication;

    this.testThreshold = testThreshold;
    this.markUnstableTest = markUnstableTest;

    this.coverageThreshold = coverageThreshold;
    this.markUnstableCoverage = markUnstableCoverage;

    this.mutationTestThreshold = mutationTestThreshold;
    this.markUnstableMutationTest = markUnstableMutationTest;
  }

  public int getPremirrorCacheThreshold() {
    return premirrorCacheThreshold;
  }

  public void setPremirrorCacheThreshold(int threshold) {
    this.premirrorCacheThreshold = threshold;
  }

  public boolean isMarkUnstablePremirrorCache() {
    return this.markUnstablePremirrorCache;
  }

  public void setMarkUnstablePremirrorCache(boolean markUnstable) {
    this.markUnstablePremirrorCache = markUnstable;
  }

  public int getSharedStateCacheThreshold() {
    return sharedStateCacheThreshold;
  }

  public void setSharedStateCacheThreshold(int threshold) {
    this.sharedStateCacheThreshold = threshold;
  }

  public boolean isMarkUnstableSharedStateCache() {
    return this.markUnstableSharedStateCache;
  }

  public void setMarkUnstableSharedStateCache(boolean markUnstable) {
    this.markUnstableSharedStateCache = markUnstable;
  }

  public double getCodeViolationThreshold() {
    return codeViolationThreshold;
  }

  public void setCodeViolationThreshold(double threshold) {
    this.codeViolationThreshold = threshold;
  }

  public boolean isMarkUnstableCodeViolation() {
    return this.markUnstableCodeViolation;
  }

  public void setMarkUnstableCodeViolation(boolean markUnstable) {
    this.markUnstableCodeViolation = markUnstable;
  }

  public int getCommentThreshold() {
    return commentThreshold;
  }

  public void setCommentThreshold(int threshold) {
    this.commentThreshold = threshold;
  }

  public boolean isMarkUnstableComment() {
    return this.markUnstableComment;
  }

  public void setMarkUnstableComment(boolean markUnstable) {
    this.markUnstableComment = markUnstable;
  }

  public long getComplexityLevel() {
    return complexityLevel;
  }

  public void setComplexityLevel(int level) {
    this.complexityLevel = level;
  }

  public int getComplexityThreshold() {
    return complexityThreshold;
  }

  public void setComplexityThreshold(int threshold) {
    this.complexityThreshold = threshold;
  }

  public boolean isMarkUnstableComplexity() {
    return this.markUnstableComplexity;
  }

  public void setMarkUnstableComplexity(boolean markUnstable) {
    this.markUnstableComplexity = markUnstable;
  }

  public int getCoverageThreshold() {
    return coverageThreshold;
  }

  public void setCoverageThreshold(int threshold) {
    this.coverageThreshold = threshold;
  }

  public boolean isMarkUnstableCoverage() {
    return this.markUnstableCoverage;
  }

  public void setMarkUnstableCoverage(boolean markUnstable) {
    this.markUnstableCoverage = markUnstable;
  }

  public int getDuplicationThreshold() {
    return duplicationThreshold;
  }

  public void setDuplicationThreshold(int threshold) {
    this.duplicationThreshold = threshold;
  }

  public boolean isMarkUnstableDuplication() {
    return this.markUnstableDuplication;
  }

  public void setMarkUnstableDuplication(boolean markUnstable) {
    this.markUnstableDuplication = markUnstable;
  }

  public int getMutationTestThreshold() {
    return mutationTestThreshold;
  }

  public void setMutationTestThreshold(int threshold) {
    this.mutationTestThreshold = threshold;
  }

  public boolean isMarkUnstableMutationTest() {
    return this.markUnstableMutationTest;
  }

  public void setMarkUnstableMutationTest(boolean markUnstable) {
    this.markUnstableMutationTest = markUnstable;
  }

  public double getRecipeViolationThreshold() {
    return recipeViolationThreshold;
  }

  public void setRecipeViolationThreshold(double threshold) {
    this.recipeViolationThreshold = threshold;
  }

  public boolean isMarkUnstableRecipeViolation() {
    return this.markUnstableRecipeViolation;
  }

  public void setMarkUnstableRecipeViolation(boolean markUnstable) {
    this.markUnstableRecipeViolation = markUnstable;
  }

  public int getTestThreshold() {
    return testThreshold;
  }

  public void setTestThreshold(int threshold) {
    this.testThreshold = threshold;
  }

  public boolean isMarkUnstableTest() {
    return this.markUnstableTest;
  }

  public void setMarkUnstableTest(boolean markUnstable) {
    this.markUnstableTest = markUnstable;
  }

  /**
   * return Criteria instance for metrics.
   */
  public Criteria getCriteria() {
    return new Criteria(
        this.premirrorCacheThreshold,
        this.sharedStateCacheThreshold,
        this.recipeViolationThreshold,
        this.commentThreshold,
        this.codeViolationThreshold,
        this.complexityLevel,
        this.complexityThreshold,
        this.duplicationThreshold,
        this.testThreshold,
        this.coverageThreshold,
        this.mutationTestThreshold);
  }
}
