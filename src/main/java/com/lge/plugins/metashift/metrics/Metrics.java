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

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Streamable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of metrics.
 *
 * @author Sung Gon Kim
 */
public final class Metrics extends NullEvaluator<Metrics> implements Queryable<Evaluator<?>> {

  /**
   * Represents the collection of evaluators.
   */
  private final Map<Class<?>, Evaluator<?>> collection;

  /**
   * Represents the criteria for evaluation.
   */
  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public Metrics(final Configuration configuration) {
    super();
    collection = new HashMap<>();
    collection.put(PremirrorCacheEvaluator.class, new PremirrorCacheEvaluator(configuration));
    collection.put(SharedStateCacheEvaluator.class, new SharedStateCacheEvaluator(configuration));
    collection.put(CodeSizeEvaluator.class, new CodeSizeEvaluator());
    collection.put(CodeViolationEvaluator.class, new CodeViolationEvaluator(configuration));
    collection.put(CommentEvaluator.class, new CommentEvaluator(configuration));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(configuration));
    collection.put(StatementCoverageEvaluator.class, new StatementCoverageEvaluator(configuration));
    collection.put(BranchCoverageEvaluator.class, new BranchCoverageEvaluator(configuration));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(configuration));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(configuration));
    collection.put(RecipeViolationEvaluator.class, new RecipeViolationEvaluator(configuration));
    collection.put(TestEvaluator.class, new TestEvaluator(configuration));
    this.configuration = configuration;
  }

  @Override
  public Evaluator<?> getPremirrorCache() {
    return collection.get(PremirrorCacheEvaluator.class);
  }

  @Override
  public Evaluator<?> getSharedStateCache() {
    return collection.get(SharedStateCacheEvaluator.class);
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
  public Evaluator<?> getStatementCoverage() {
    return collection.get(StatementCoverageEvaluator.class);
  }

  @Override
  public Evaluator<?> getBranchCoverage() {
    return collection.get(BranchCoverageEvaluator.class);
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
  public boolean isStable() {
    return collection.values().stream().allMatch(Evaluator::isStable);
  }

  @Override
  protected void parseImpl(final Streamable c) {
    collection
        .put(PremirrorCacheEvaluator.class, new PremirrorCacheEvaluator(configuration).parse(c));
    collection.put(SharedStateCacheEvaluator.class,
        new SharedStateCacheEvaluator(configuration).parse(c));
    collection.put(CodeSizeEvaluator.class, new CodeSizeEvaluator().parse(c));
    collection
        .put(CodeViolationEvaluator.class, new CodeViolationEvaluator(configuration).parse(c));
    collection.put(CommentEvaluator.class, new CommentEvaluator(configuration).parse(c));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(configuration).parse(c));
    collection.put(StatementCoverageEvaluator.class,
        new StatementCoverageEvaluator(configuration).parse(c));
    collection
        .put(BranchCoverageEvaluator.class, new BranchCoverageEvaluator(configuration).parse(c));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(configuration).parse(c));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(configuration).parse(c));
    collection.put(RecipeViolationEvaluator.class,
        new RecipeViolationEvaluator(configuration).parse(c));
    collection.put(TestEvaluator.class, new TestEvaluator(configuration).parse(c));

    setDenominator(collection.values().stream().filter(Evaluator::isAvailable).count());
    setNumerator(collection.values().stream().filter(Evaluator::isQualified).count());
  }
}
