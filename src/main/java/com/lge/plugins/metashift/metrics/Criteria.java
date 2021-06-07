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
 * Represents the quality metric criteria.
 *
 * @author Sung Gon Kim
 */
public class Criteria {

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
  public Criteria(final int premirrorCacheThreshold,
      final int sharedStateCacheThreshold,
      final double recipeViolationThreshold,
      final int commentThreshold,
      final double codeViolationThreshold,
      final long complexityLevel,
      final int complexityThreshold,
      final int duplicationThreshold,
      final int testThreshold,
      final int coverageThreshold,
      final int mutationTestThreshold) {
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
  }

  /**
   * Default constructor.
   */
  public Criteria() {
    this(80, 80, 0.1, 30, 0.1, 4, 10, 10, 95, 60, 85);
  }

  /**
   * Returns the premirror cache availability threshold.
   *
   * @return premirror cache threshold
   */
  public int getPremirrorCacheThreshold() {
    return premirrorCacheThreshold;
  }

  /**
   * Sets the premirror cache availability threshold.
   *
   * @param threshold for the premirror cache availability
   */
  public void setPremirrorCacheThreshold(final int threshold) {
    premirrorCacheThreshold = threshold;
  }

  /**
   * Returns the shared state cache availability threshold.
   *
   * @return shared state cache threshold
   */
  public int getSharedStateCacheThreshold() {
    return sharedStateCacheThreshold;
  }

  /**
   * Sets the shared state cache availability threshold.
   *
   * @param threshold for the shared state cache availability
   */
  public void setSharedStateCacheThreshold(final int threshold) {
    sharedStateCacheThreshold = threshold;
  }

  /**
   * Returns the code violation threshold.
   *
   * @return code violation threshold
   */
  public double getCodeViolationThreshold() {
    return codeViolationThreshold;
  }

  /**
   * Sets the code violation threshold.
   *
   * @param threshold for the code violations
   */
  public void setCodeViolationThreshold(final double threshold) {
    codeViolationThreshold = threshold;
  }

  /**
   * Returns the comment threshold.
   *
   * @return comment threshold
   */
  public int getCommentThreshold() {
    return commentThreshold;
  }

  /**
   * Sets the comment threshold.
   *
   * @param threshold for the comments
   */
  public void setCommentThreshold(final int threshold) {
    commentThreshold = threshold;
  }

  /**
   * Returns the complexity level.
   *
   * @return complexity level
   */
  public long getComplexityLevel() {
    return complexityLevel;
  }

  /**
   * Sets the complexity level.
   *
   * @param level fot the complexity
   */
  public void setComplexityLevel(final long level) {
    complexityLevel = level;
  }

  /**
   * Returns the complexity threshold.
   *
   * @return complexity threshold
   */
  public int getComplexityThreshold() {
    return complexityThreshold;
  }

  /**
   * Sets the complexity threshold.
   *
   * @param threshold for the complexity
   */
  public void setComplexityThreshold(final int threshold) {
    complexityThreshold = threshold;
  }

  /**
   * Returns the coverage threshold.
   *
   * @return coverage threshold
   */
  public int getCoverageThreshold() {
    return coverageThreshold;
  }

  /**
   * Sets the coverage threshold.
   *
   * @param threshold for the coverage
   */
  public void setCoverageThreshold(final int threshold) {
    coverageThreshold = threshold;
  }

  /**
   * Returns the duplication threshold.
   *
   * @return duplication threshold
   */
  public int getDuplicationThreshold() {
    return duplicationThreshold;
  }

  /**
   * Sets the duplication threshold.
   *
   * @param threshold for the duplications
   */
  public void setDuplicationThreshold(final int threshold) {
    duplicationThreshold = threshold;
  }

  /**
   * Returns the mutation test threshold.
   *
   * @return mutation test threshold.
   */
  public int getMutationTestThreshold() {
    return mutationTestThreshold;
  }

  /**
   * Sets the mutation test threshold.
   *
   * @param threshold for the mutation test
   */
  public void setMutationTestThreshold(final int threshold) {
    mutationTestThreshold = threshold;
  }

  /**
   * Returns the recipe violation threshold.
   *
   * @return recipe violation threshold
   */
  public double getRecipeViolationThreshold() {
    return recipeViolationThreshold;
  }

  /**
   * Sets the recipe violation threshold.
   *
   * @param threshold for the recipe violations
   */
  public void setRecipeViolationThreshold(final double threshold) {
    recipeViolationThreshold = threshold;
  }

  /**
   * Returns the test threshold.
   *
   * @return test threshold
   */
  public int getTestThreshold() {
    return testThreshold;
  }

  /**
   * Sets the test threshold.
   *
   * @param threshold for the test
   */
  public void setTestThreshold(final int threshold) {
    testThreshold = threshold;
  }
}
