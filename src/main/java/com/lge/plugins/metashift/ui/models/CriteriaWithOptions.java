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

package com.lge.plugins.metashift.ui.models;

import com.lge.plugins.metashift.metrics.Criteria;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Config form of the quality metric criteria.
 */
public class CriteriaWithOptions extends Criteria {

  private boolean markUnstablePremirrorCache;
  private boolean markUnstableSharedStateCache;
  private boolean markUnstableCodeViolation;
  private boolean markUnstableComment;
  private boolean markUnstableComplexity;
  private boolean markUnstableCoverage;
  private boolean markUnstableDuplication;
  private boolean markUnstableMutationTest;
  private boolean markUnstableRecipeViolation;
  private boolean markUnstableTest;

  /**
   * Default constructor.
   */
  public CriteriaWithOptions() {
    this(80, 80, 0.1, 30, 0.1, 4, 10, 10, 95, 60, 85,
        true, true, true, true, true, true, true, true, true, true);
  }

  /**
   * Default constructor.
   */
  @DataBoundConstructor
  public CriteriaWithOptions(final int premirrorCacheThreshold,
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
      final boolean markUnstablePremirrorCache,
      final boolean markUnstableSharedStateCache,
      final boolean markUnstableRecipeViolation,
      final boolean markUnstableComment,
      final boolean markUnstableCodeViolation,
      final boolean markUnstableComplexity,
      final boolean markUnstableDuplication,
      final boolean markUnstableTest,
      final boolean markUnstableCoverage,
      final boolean markUnstableMutationTest) {
    super(premirrorCacheThreshold,
        sharedStateCacheThreshold,
        recipeViolationThreshold,
        commentThreshold,
        codeViolationThreshold,
        complexityLevel,
        complexityThreshold,
        duplicationThreshold,
        testThreshold,
        coverageThreshold,
        mutationTestThreshold);
    this.markUnstablePremirrorCache = markUnstablePremirrorCache;
    this.markUnstableSharedStateCache = markUnstableSharedStateCache;
    this.markUnstableRecipeViolation = markUnstableRecipeViolation;
    this.markUnstableComment = markUnstableComment;
    this.markUnstableCodeViolation = markUnstableCodeViolation;
    this.markUnstableComplexity = markUnstableComplexity;
    this.markUnstableDuplication = markUnstableDuplication;
    this.markUnstableTest = markUnstableTest;
    this.markUnstableCoverage = markUnstableCoverage;
    this.markUnstableMutationTest = markUnstableMutationTest;
  }

  public boolean isMarkUnstablePremirrorCache() {
    return this.markUnstablePremirrorCache;
  }

  public void setMarkUnstablePremirrorCache(boolean markUnstable) {
    this.markUnstablePremirrorCache = markUnstable;
  }

  public boolean isMarkUnstableSharedStateCache() {
    return this.markUnstableSharedStateCache;
  }

  public void setMarkUnstableSharedStateCache(boolean markUnstable) {
    this.markUnstableSharedStateCache = markUnstable;
  }

  public boolean isMarkUnstableCodeViolation() {
    return this.markUnstableCodeViolation;
  }

  public void setMarkUnstableCodeViolation(boolean markUnstable) {
    this.markUnstableCodeViolation = markUnstable;
  }

  public boolean isMarkUnstableComment() {
    return this.markUnstableComment;
  }

  public void setMarkUnstableComment(boolean markUnstable) {
    this.markUnstableComment = markUnstable;
  }

  public boolean isMarkUnstableComplexity() {
    return this.markUnstableComplexity;
  }

  public void setMarkUnstableComplexity(boolean markUnstable) {
    this.markUnstableComplexity = markUnstable;
  }

  public boolean isMarkUnstableCoverage() {
    return this.markUnstableCoverage;
  }

  public void setMarkUnstableCoverage(boolean markUnstable) {
    this.markUnstableCoverage = markUnstable;
  }

  public boolean isMarkUnstableDuplication() {
    return this.markUnstableDuplication;
  }

  public void setMarkUnstableDuplication(boolean markUnstable) {
    this.markUnstableDuplication = markUnstable;
  }

  public boolean isMarkUnstableMutationTest() {
    return this.markUnstableMutationTest;
  }

  public void setMarkUnstableMutationTest(boolean markUnstable) {
    this.markUnstableMutationTest = markUnstable;
  }

  public boolean isMarkUnstableRecipeViolation() {
    return this.markUnstableRecipeViolation;
  }

  public void setMarkUnstableRecipeViolation(boolean markUnstable) {
    this.markUnstableRecipeViolation = markUnstable;
  }

  public boolean isMarkUnstableTest() {
    return this.markUnstableTest;
  }

  public void setMarkUnstableTest(boolean markUnstable) {
    this.markUnstableTest = markUnstable;
  }
}
