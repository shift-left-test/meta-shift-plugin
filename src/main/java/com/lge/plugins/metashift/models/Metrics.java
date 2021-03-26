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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of metrics.
 *
 * @author Sung Gon Kim
 */
public final class Metrics extends Visitor implements Counter, Qualifier {
  /**
   * Represents the collection of quality metrics.
   */
  private Map<Class<? extends Qualifier>, Qualifier> collection;
  /**
   * Represents the threshold of the qualification.
   */
  private float threshold;

  /**
   * Default constructor.
   */
  public Metrics(final Criteria criteria) {
    collection = new HashMap<>();
    collection.put(CacheQualifier.class,
                   new CacheQualifier(criteria.getCacheThreshold()));
    collection.put(RecipeViolationQualifier.class,
                   new RecipeViolationQualifier(criteria.getRecipeViolationThreshold()));
    collection.put(CommentQualifier.class,
                   new CommentQualifier(criteria.getCommentThreshold()));
    collection.put(CodeViolationQualifier.class,
                   new CodeViolationQualifier(criteria.getCodeViolationThreshold()));
    collection.put(ComplexityQualifier.class,
                   new ComplexityQualifier(criteria.getComplexityLevel(),
                                           criteria.getComplexityThreshold()));
    // TODO(sunggon82.kim): CoverageQualifer required
    // collection.put(CoverageQualifier.class,
    //               new CoverageQualifier(criteria.getCoverageThreshold()));
    collection.put(DuplicationQualifier.class,
                   new DuplicationQualifier(criteria.getDuplicationThreshold()));
    collection.put(TestQualifier.class,
                   new TestQualifier(criteria.getTestThreshold()));
    collection.put(MutationTestQualifier.class,
                   new MutationTestQualifier(criteria.getMutationTestThreshold()));
    this.threshold = criteria.getOverallThreshold();
  }

  /**
   * Returns the relevent Qualifier object based on the given class type.
   *
   * @param clazz of the object type to return
   * @return Qualifier object
   */
  public Qualifier collection(final Class<? extends Qualifier> clazz) {
    return collection.get(clazz);
  }

  @Override
  public int getDenominator() {
    return (int) collection.values().stream()
        .filter(o -> o.isAvailable()).count();
  }

  @Override
  public int getNumerator() {
    return (int) collection.values().stream()
        .filter(o -> o.isAvailable() && o.isQualified()).count();
  }

  @Override
  public boolean isAvailable() {
    return getDenominator() > 0;
  }

  @Override
  public boolean isQualified() {
    if (getDenominator() == 0) {
      return false;
    }
    return ((float) getNumerator() / (float) getDenominator()) >= threshold;
  }

  @Override
  public void visit(final CacheList object) {
    object.accept((CacheQualifier) collection.get(CacheQualifier.class));
  }

  @Override
  public void visit(final CommentList object) {
    object.accept((CommentQualifier) collection.get(CommentQualifier.class));
  }

  @Override
  public void visit(final RecipeViolationList object) {
    object.accept((RecipeViolationQualifier) collection.get(RecipeViolationQualifier.class));
  }

  @Override
  public void visit(final CodeViolationList object) {
    object.accept((CodeViolationQualifier) collection.get(CodeViolationQualifier.class));
  }

  @Override
  public void visit(final ComplexityList object) {
    object.accept((ComplexityQualifier) collection.get(ComplexityQualifier.class));
  }

  @Override
  public void visit(final DuplicationList object) {
    object.accept((DuplicationQualifier) collection.get(DuplicationQualifier.class));
  }

  @Override
  public void visit(final TestList object) {
    object.accept((TestQualifier) collection.get(TestQualifier.class));
  }

  @Override
  public void visit(final MutationTestList object) {
    object.accept((MutationTestQualifier) collection.get(MutationTestQualifier.class));
  }
}
