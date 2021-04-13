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
public final class Criteria {

  /**
   * Represents the threshold for the cache availability.
   */
  private double cacheThreshold;

  /**
   * Represents the threshold for the code violations.
   */
  private double codeViolationThreshold;

  /**
   * Represents the threshold for the comments.
   */
  private double commentThreshold;

  /**
   * Represents the level of the complexity.
   */
  private long complexityLevel;

  /**
   * Represents the threshold for the complexity.
   */
  private double complexityThreshold;

  /**
   * Represents the threshold for the coverage.
   */
  private double coverageThreshold;

  /**
   * Represents the threshold for the duplications.
   */
  private double duplicationThreshold;

  /**
   * Represents the threshold for the mutation test.
   */
  private double mutationTestThreshold;

  /**
   * Represents the overall threshold.
   */
  private double overallThreshold;

  /**
   * Represents the threshold for the recipe violations.
   */
  private double recipeViolationThreshold;

  /**
   * Represents the threshold for the test.
   */
  private double testThreshold;

  /**
   * Default constructor.
   */
  public Criteria() {
    this(0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  /**
   * Default constructor.
   *
   * @param overallThreshold         overall threshold
   * @param cacheThreshold           cache availability threshold
   * @param recipeViolationThreshold recipe violation threshold
   * @param commentThreshold         comment threshold
   * @param codeViolationThreshold   code violation threshold
   * @param complexityLevel          complexity level
   * @param complexityThreshold      complexity threshold
   * @param duplicationThreshold     duplication threshold
   * @param testThreshold            test threshold
   * @param coverageThreshold        coverage threshold
   * @param mutationTestThreshold    mutation test threshold
   */
  @DataBoundConstructor
  public Criteria(final double overallThreshold,
      final double cacheThreshold,
      final double recipeViolationThreshold,
      final double commentThreshold,
      final double codeViolationThreshold,
      final long complexityLevel,
      final double complexityThreshold,
      final double duplicationThreshold,
      final double testThreshold,
      final double coverageThreshold,
      final double mutationTestThreshold) {
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
  public double getCacheThreshold() {
    return cacheThreshold;
  }

  /**
   * Sets the cache availability threshold.
   *
   * @param threshold for the cache availability
   */
  public void setCacheThreshold(final double threshold) {
    cacheThreshold = threshold;
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
  public double getCommentThreshold() {
    return commentThreshold;
  }

  /**
   * Sets the comment threshold.
   *
   * @param threshold for the comments
   */
  public void setCommentThreshold(final double threshold) {
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
  public double getComplexityThreshold() {
    return complexityThreshold;
  }

  /**
   * Sets the complexity threshold.
   *
   * @param threshold for the complexity
   */
  public void setComplexityThreshold(final double threshold) {
    complexityThreshold = threshold;
  }

  /**
   * Returns the coverage threshold.
   *
   * @return coverage threshold
   */
  public double getCoverageThreshold() {
    return coverageThreshold;
  }

  /**
   * Sets the coverage threshold.
   *
   * @param threshold for the coverage
   */
  public void setCoverageThreshold(final double threshold) {
    coverageThreshold = threshold;
  }

  /**
   * Returns the duplication threshold.
   *
   * @return duplication threshold
   */
  public double getDuplicationThreshold() {
    return duplicationThreshold;
  }

  /**
   * Sets the duplication threshold.
   *
   * @param threshold for the duplications
   */
  public void setDuplicationThreshold(final double threshold) {
    duplicationThreshold = threshold;
  }

  /**
   * Returns the mutation test threshold.
   *
   * @return mutation test threshold.
   */
  public double getMutationTestThreshold() {
    return mutationTestThreshold;
  }

  /**
   * Sets the mutation test threshold.
   *
   * @param threshold for the mutation test
   */
  public void setMutationTestThreshold(final double threshold) {
    mutationTestThreshold = threshold;
  }

  /**
   * Returns the overall threshold.
   *
   * @return overall threshold
   */
  public double getOverallThreshold() {
    return overallThreshold;
  }

  /**
   * Sets the overall threshold.
   *
   * @param threshold for the project
   */
  public void setOverallThreshold(final double threshold) {
    overallThreshold = threshold;
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
  public double getTestThreshold() {
    return testThreshold;
  }

  /**
   * Sets the test threshold.
   *
   * @param threshold for the test
   */
  public void setTestThreshold(final double threshold) {
    testThreshold = threshold;
  }
}
