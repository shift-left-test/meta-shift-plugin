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

package com.lge.plugins.metashift.metrics2;

import com.lge.plugins.metashift.models.Collectable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of metrics.
 *
 * @author Sung Gon Kim
 */
public final class Metrics extends Evaluator<Metrics> {

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
    collection.put(CodeViolationEvaluator.class, new CodeViolationEvaluator(new Criteria()));
    collection.put(CommentEvaluator.class, new CommentEvaluator(new Criteria()));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(new Criteria()));
    collection.put(CoverageEvaluator.class, new CoverageEvaluator(new Criteria()));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(new Criteria()));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(new Criteria()));
    collection.put(RecipeViolationEvaluator.class, new RecipeViolationEvaluator(new Criteria()));
    collection.put(SizeEvaluator.class, new SizeEvaluator());
    collection.put(TestEvaluator.class, new TestEvaluator(new Criteria()));
    this.criteria = criteria;
  }

  /**
   * Returns the cache availability evaluator.
   *
   * @return an evaluator object
   */
  public CacheEvaluator getCacheAvailability() {
    return (CacheEvaluator) collection.get(CacheEvaluator.class);
  }

  /**
   * Returns the code violation evaluator.
   *
   * @return an evaluator object
   */
  public CodeViolationEvaluator getCodeViolations() {
    return (CodeViolationEvaluator) collection.get(CodeViolationEvaluator.class);
  }

  /**
   * Returns the comment evaluator.
   *
   * @return an evaluator object
   */
  public CommentEvaluator getComments() {
    return (CommentEvaluator) collection.get(CommentEvaluator.class);
  }

  /**
   * Returns the complexity evaluator.
   *
   * @return an evaluator object
   */
  public ComplexityEvaluator getComplexity() {
    return (ComplexityEvaluator) collection.get(ComplexityEvaluator.class);
  }

  /**
   * Returns the coverage evaluator.
   *
   * @return an evaluator object
   */
  public CoverageEvaluator getCoverage() {
    return (CoverageEvaluator) collection.get(CoverageEvaluator.class);
  }

  /**
   * Returns the duplication evaluator.
   *
   * @return an evaluator object
   */
  public DuplicationEvaluator getDuplications() {
    return (DuplicationEvaluator) collection.get(DuplicationEvaluator.class);
  }

  /**
   * Returns the mutation test evaluator.
   *
   * @return an evaluator object
   */
  public MutationTestEvaluator getMutationTest() {
    return (MutationTestEvaluator) collection.get(MutationTestEvaluator.class);
  }

  /**
   * Returns the recipe violation evaluator.
   *
   * @return an evaluator object
   */
  public RecipeViolationEvaluator getRecipeViolations() {
    return (RecipeViolationEvaluator) collection.get(RecipeViolationEvaluator.class);
  }

  /**
   * Returns the size evaluator.
   *
   * @return an evaluator object
   */
  public SizeEvaluator getSize() {
    return (SizeEvaluator) collection.get(SizeEvaluator.class);
  }

  /**
   * Returns the test evaluator.
   *
   * @return an evaluator object
   */
  public TestEvaluator getTest() {
    return (TestEvaluator) collection.get(TestEvaluator.class);
  }

  @Override
  protected void parseImpl(final Collectable c) {
    collection.put(CacheEvaluator.class, new CacheEvaluator(criteria).parse(c));
    collection.put(CodeViolationEvaluator.class, new CodeViolationEvaluator(criteria).parse(c));
    collection.put(CommentEvaluator.class, new CommentEvaluator(criteria).parse(c));
    collection.put(ComplexityEvaluator.class, new ComplexityEvaluator(criteria).parse(c));
    collection.put(CoverageEvaluator.class, new CoverageEvaluator(criteria).parse(c));
    collection.put(DuplicationEvaluator.class, new DuplicationEvaluator(criteria).parse(c));
    collection.put(MutationTestEvaluator.class, new MutationTestEvaluator(criteria).parse(c));
    collection.put(RecipeViolationEvaluator.class,
        new RecipeViolationEvaluator(criteria).parse(c));
    collection.put(SizeEvaluator.class, new SizeEvaluator().parse(c));
    collection.put(TestEvaluator.class, new TestEvaluator(criteria).parse(c));

    setDenominator(collection.values().stream().filter(Evaluator::isAvailable).count());
    setNumerator(collection.values().stream().filter(Evaluator::isQualified).count());
  }
}
