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

/**
 * Represents the quality metric criteria.
 *
 * @author Sung Gon Kim
 */
public final class Criteria {
  /**
   * Represents the threshold for the cache availabilty.
   */
  private float cacheThreshold;
  /**
   * Represents the threshold for the code violations.
   */
  private float codeViolationThreshold;
  /**
   * Represents the threshold for the comments.
   */
  private float commentThreshold;
  /**
   * Represents the level of the complexity.
   */
  private int complexityLevel;
  /**
   * Represents the threshold for the complexity.
   */
  private float complexityThreshold;
  /**
   * Represents the threshold for the coverage.
   */
  private float coverageThreshold;
  /**
   * Represents the threshold for the duplications.
   */
  private float duplicationThreshold;
  /**
   * Represents the threshold for the mutation test.
   */
  private float mutationTestThreshold;
  /**
   * Represents the overall threshold.
   */
  private float overallThreshold;
  /**
   * Represents the threshold for the recipe violations.
   */
  private float recipeViolationThreshold;
  /**
   * Represents the threshold for the test.
   */
  private float testThreshold;

  /**
   * Default constructor.
   */
  public Criteria() {
    this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  /**
   * Default constructor.
   *
   * @param overallThreshold overall threshold
   * @param cacheThreshold cache availability threshold
   * @param recipeViolationThreshold recipe violation threshold
   * @param commentThreshold comment threshold
   * @param codeViolationThreshold code violation threshold
   * @param complexityLevel complexity level
   * @param complexityThreshold complexity threshold
   * @param duplicationThreshold duplication threshold
   * @param testThreshold test threshold
   * @param coverageThreshold coverage threshold
   * @param mutationTestThreshold mutation test threshold
   */
  public Criteria(final float overallThreshold,
                  final float cacheThreshold,
                  final float recipeViolationThreshold,
                  final float commentThreshold,
                  final float codeViolationThreshold,
                  final int complexityLevel,
                  final float complexityThreshold,
                  final float duplicationThreshold,
                  final float testThreshold,
                  final float coverageThreshold,
                  final float mutationTestThreshold) {
    this.overallThreshold = overallThreshold;
    this.cacheThreshold = cacheThreshold;
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
   * Returns the cache availability threshold.
   *
   * @return cache threshold
   */
  public float getCacheThreshold() {
    return cacheThreshold;
  }

  /**
   * Sets the cahce availability threshold.
   *
   * @param threshold for the cache availability
   */
  public void setCacheThreshold(final float threshold) {
    cacheThreshold = threshold;
  }

  /**
   * Returns the code violation threshold.
   *
   * @return code violation threshold
   */
  public float getCodeViolationThreshold() {
    return codeViolationThreshold;
  }

  /**
   * Sets the code violation threshold.
   *
   * @param threshold for the code violations
   */
  public void setCodeViolationThreshold(final float threshold) {
    codeViolationThreshold = threshold;
  }

  /**
   * Returns the comment threshold.
   *
   * @return comment threshold
   */
  public float getCommentThreshold() {
    return commentThreshold;
  }

  /**
   * Sets the comment threshold.
   *
   * @param threshold for the comments
   */
  public void setCommentThreshold(final float threshold) {
    commentThreshold = threshold;
  }

  /**
   * Returns the complexity level.
   *
   * @return complexity level
   */
  public int getComplexityLevel() {
    return complexityLevel;
  }

  /**
   * Sets the complexity level.
   *
   * @param level fot the complexity
   */
  public void setComplexityLevel(final int level) {
    complexityLevel = level;
  }

  /**
   * Returns the complexity threshold.
   *
   * @return complexity threshold
   */
  public float getComplexityThreshold() {
    return complexityThreshold;
  }

  /**
   * Sets the complexity threshold.
   *
   * @param threshold for the complexity
   */
  public void setComplexityThreshold(final float threshold) {
    complexityThreshold = threshold;
  }

  /**
   * Returns the coverage threshold.
   *
   * @return coverage threshold
   */
  public float getCoverageThreshold() {
    return coverageThreshold;
  }

  /**
   * Sets the coverage threshold.
   *
   * @param threshold for the coverage
   */
  public void setCoverageThreshold(final float threshold) {
    coverageThreshold = threshold;
  }

  /**
   * Returns the duplication threshold.
   *
   * @return duplication threshold
   */
  public float getDuplicationThreshold() {
    return duplicationThreshold;
  }

  /**
   * Sets the duplication threshold.
   *
   * @param threshold for the duplications
   */
  public void setDuplicationThreshold(final float threshold) {
    duplicationThreshold = threshold;
  }

  /**
   * Returns the mutation test threshold.
   *
   * @return mutation test threshold.
   */
  public float getMutationTestThreshold() {
    return mutationTestThreshold;
  }

  /**
   * Sets the mutation test threshold.
   *
   * @param threshold for the mutation test
   */
  public void setMutationTestThreshold(final float threshold) {
    mutationTestThreshold = threshold;
  }

  /**
   * Returns the overall threshold.
   *
   * @return overall threshold
   */
  public float getOverallThreshold() {
    return overallThreshold;
  }

  /**
   * Sets the overal threshold.
   *
   * @param threshold for the project
   */
  public void setOverallThreshold(final float threshold) {
    overallThreshold = threshold;
  }

  /**
   * Returns the recipe violation threshold.
   *
   * @return recipe violation threshold
   */
  public float getRecipeViolationThreshold() {
    return recipeViolationThreshold;
  }

  /**
   * Sets the recipe violation threshold.
   *
   * @param threshold for the recipe violations
   */
  public void setRecipeViolationThreshold(final float threshold) {
    recipeViolationThreshold = threshold;
  }

  /**
   * Returns the test threshold.
   *
   * @return test threshold
   */
  public float getTestThreshold() {
    return testThreshold;
  }

  /**
   * Sets the test threshold.
   *
   * @param threshold for the test
   */
  public void setTestThreshold(final float threshold) {
    testThreshold = threshold;
  }
}
