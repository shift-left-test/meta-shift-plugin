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

import com.lge.plugins.metashift.models.CacheList;
import com.lge.plugins.metashift.models.CodeViolationList;
import com.lge.plugins.metashift.models.CommentList;
import com.lge.plugins.metashift.models.ComplexityList;
import com.lge.plugins.metashift.models.DuplicationList;
import com.lge.plugins.metashift.models.MutationTestList;
import com.lge.plugins.metashift.models.RecipeViolationList;
import com.lge.plugins.metashift.models.SizeList;
import com.lge.plugins.metashift.models.TestList;

/**
 * Represents a set of metrics.
 *
 * @author Sung Gon Kim
 */
public final class Metrics extends Qualifier<Qualifier<?>> {

  /**
   * Default constructor.
   */
  public Metrics(final Criteria criteria) {
    super(criteria.getOverallThreshold(),
        new CacheQualifier(criteria.getCacheThreshold()),
        new RecipeViolationQualifier(criteria.getRecipeViolationThreshold()),
        new SizeQualifier(),
        new CommentQualifier(criteria.getCommentThreshold()),
        new CodeViolationQualifier(criteria.getCodeViolationThreshold()),
        new ComplexityQualifier(criteria.getComplexityLevel(),
            criteria.getComplexityThreshold()),
        new DuplicationQualifier(criteria.getDuplicationThreshold()),
        new TestQualifier(criteria.getTestThreshold()),
        new MutationTestQualifier(criteria.getMutationTestThreshold()));
  }

  @Override
  public int getDenominator() {
    return (int) getCollection().values().stream()
        .filter(Qualifier::isAvailable).count();
  }

  @Override
  public int getNumerator() {
    return (int) getCollection().values().stream()
        .filter(o -> o.isAvailable() && o.isQualified()).count();
  }

  @Override
  public boolean isQualified() {
    if (getDenominator() == 0) {
      return false;
    }
    return ((float) getNumerator() / (float) getDenominator()) >= getThreshold();
  }

  @Override
  public void visit(final CacheList object) {
    object.accept(get(CacheQualifier.class));
  }

  @Override
  public void visit(final SizeList object) {
    object.accept(get(SizeQualifier.class));
  }

  @Override
  public void visit(final CommentList object) {
    object.accept(get(CommentQualifier.class));
  }

  @Override
  public void visit(final RecipeViolationList object) {
    object.accept(get(RecipeViolationQualifier.class));
  }

  @Override
  public void visit(final CodeViolationList object) {
    object.accept(get(CodeViolationQualifier.class));
  }

  @Override
  public void visit(final ComplexityList object) {
    object.accept(get(ComplexityQualifier.class));
  }

  @Override
  public void visit(final DuplicationList object) {
    object.accept(get(DuplicationQualifier.class));
  }

  @Override
  public void visit(final TestList object) {
    object.accept(get(TestQualifier.class));
  }

  @Override
  public void visit(final MutationTestList object) {
    object.accept(get(MutationTestQualifier.class));
  }
}
