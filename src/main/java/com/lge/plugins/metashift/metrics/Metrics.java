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

import com.lge.plugins.metashift.models.Streamable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of metrics.
 *
 * @author Sung Gon Kim
 */
public final class Metrics extends Evaluator<Metrics> implements Queryable<Evaluator<?>> {

  /**
   * Represents the collection of evaluators.
   */
  private final Map<Class<?>, Evaluator<?>> collection;

  /**
   * Represents the criteria for evaluation.
   */
  private final Criteria criteria;

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public Metrics(final Criteria criteria) {
    super(criteria.getOverallThreshold());
    collection = new HashMap<>();
    collection.put(CacheEvaluator.class, new CacheEvaluator(new Criteria()));
    collection.put(CodeSizeEvaluator.class, new CodeSizeEvaluator());
    collection.put(CodeViolationEvaluator.class, new CodeViolationEvaluator(new Criteria()));
    collection.put(CommentEvaluator.class, new CommentEvaluator(new Criteria()));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(new Criteria()));
    collection.put(CoverageEvaluator.class, new CoverageEvaluator(new Criteria()));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(new Criteria()));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(new Criteria()));
    collection.put(RecipeViolationEvaluator.class, new RecipeViolationEvaluator(new Criteria()));
    collection.put(TestEvaluator.class, new TestEvaluator(new Criteria()));
    this.criteria = criteria;
  }

  @Override
  public Evaluator<?> getCacheAvailability() {
    return collection.get(CacheEvaluator.class);
  }

  /**
   * Returns the code size evaluator.
   *
   * @return an evaluator object
   */
  public CodeSizeEvaluator getCodeSize() {
    return (CodeSizeEvaluator) collection.get(CodeSizeEvaluator.class);
  }

  @Override
  public Evaluator<?> getCodeViolations() {
    return collection.get(CodeViolationEvaluator.class);
  }

  @Override
  public Evaluator<?> getComments() {
    return collection.get(CommentEvaluator.class);
  }

  @Override
  public Evaluator<?> getComplexity() {
    return collection.get(ComplexityEvaluator.class);
  }

  @Override
  public Evaluator<?> getCoverage() {
    return collection.get(CoverageEvaluator.class);
  }

  @Override
  public Evaluator<?> getDuplications() {
    return collection.get(DuplicationEvaluator.class);
  }

  @Override
  public Evaluator<?> getMutationTest() {
    return collection.get(MutationTestEvaluator.class);
  }

  @Override
  public Evaluator<?> getRecipeViolations() {
    return collection.get(RecipeViolationEvaluator.class);
  }

  @Override
  public Evaluator<?> getTest() {
    return collection.get(TestEvaluator.class);
  }

  @Override
  protected void parseImpl(final Streamable c) {
    collection.put(CacheEvaluator.class, new CacheEvaluator(criteria).parse(c));
    collection.put(CodeSizeEvaluator.class, new CodeSizeEvaluator().parse(c));
    collection.put(CodeViolationEvaluator.class, new CodeViolationEvaluator(criteria).parse(c));
    collection.put(CommentEvaluator.class, new CommentEvaluator(criteria).parse(c));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(criteria).parse(c));
    collection.put(CoverageEvaluator.class, new CoverageEvaluator(criteria).parse(c));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(criteria).parse(c));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(criteria).parse(c));
    collection.put(RecipeViolationEvaluator.class,
        new RecipeViolationEvaluator(criteria).parse(c));
    collection.put(TestEvaluator.class, new TestEvaluator(criteria).parse(c));

    setDenominator(collection.values().stream().filter(Evaluator::isAvailable).count());
    setNumerator(collection.values().stream().filter(Evaluator::isQualified).count());
  }
}
