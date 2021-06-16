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

import com.lge.plugins.metashift.models.Recipes;
import java.util.List;

/**
 * Counts the number of qualified recipes based on the given criteria.
 *
 * @author Sung Gon Kim
 */
public final class QualifiedRecipeCounter extends GroupParser<Counter> {

  /**
   * Represents the tested recipe counter.
   */
  private Counter testedRecipes;

  /**
   * Represents the criteria object.
   */
  private final Criteria criteria;

  /**
   * Default constructor.
   *
   * @param criteria for collection
   */
  public QualifiedRecipeCounter(final Criteria criteria) {
    super(new Counter());
    testedRecipes = new Counter();
    this.criteria = criteria;
  }

  /**
   * Returns the tested recipe counter object.
   *
   * @return counter object
   */
  public Counter getTestedRecipes() {
    return testedRecipes;
  }

  /**
   * Counts the objects by the evaluator.
   *
   * @param recipes   to count
   * @param evaluator for calculation
   * @param <T>       class type
   * @return Counter object
   */
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private <T extends Evaluator<T>> Counter countBy(Recipes recipes, Evaluator<T> evaluator) {
    return new Counter(
        recipes.stream().filter(o -> evaluator.parse(o).isAvailable()).count(),
        recipes.stream().filter(o -> evaluator.parse(o).isQualified()).count()
    );
  }

  @Override
  public void parse(final Recipes recipes) {
    setPremirrorCache(countBy(recipes, new PremirrorCacheEvaluator(criteria)));
    setSharedStateCache(countBy(recipes, new SharedStateCacheEvaluator(criteria)));
    setCodeViolations(countBy(recipes, new CodeViolationEvaluator(criteria)));
    setComments(countBy(recipes, new CommentEvaluator(criteria)));
    setComplexity(countBy(recipes, new ComplexityEvaluator(criteria)));
    setCoverage(countBy(recipes, new CoverageEvaluator(criteria)));
    setDuplications(countBy(recipes, new DuplicationEvaluator(criteria)));
    setMutationTest(countBy(recipes, new MutationTestEvaluator(criteria)));
    setRecipeViolations(countBy(recipes, new RecipeViolationEvaluator(criteria)));
    setTest(countBy(recipes, new TestEvaluator(criteria)));
    testedRecipes = new Counter(recipes.size(), getTest().getDenominator());
  }

  @Override
  public void parse(final List<Metrics> metrics) {
    setPremirrorCache(new Counter(
        metrics.stream().map(Metrics::getPremirrorCache).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getPremirrorCache).filter(Evaluator::isQualified).count()
    ));
    setSharedStateCache(new Counter(
        metrics.stream().map(Metrics::getSharedStateCache).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getSharedStateCache).filter(Evaluator::isQualified).count()
    ));
    setCodeViolations(new Counter(
        metrics.stream().map(Metrics::getCodeViolations).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getCodeViolations).filter(Evaluator::isQualified).count()
    ));
    setComments(new Counter(
        metrics.stream().map(Metrics::getComments).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getComments).filter(Evaluator::isQualified).count()
    ));
    setComplexity(new Counter(
        metrics.stream().map(Metrics::getComplexity).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getComplexity).filter(Evaluator::isQualified).count()
    ));
    setCoverage(new Counter(
        metrics.stream().map(Metrics::getCoverage).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getCoverage).filter(Evaluator::isQualified).count()
    ));
    setDuplications(new Counter(
        metrics.stream().map(Metrics::getDuplications).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getDuplications).filter(Evaluator::isQualified).count()
    ));
    setMutationTest(new Counter(
        metrics.stream().map(Metrics::getMutationTest).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getMutationTest).filter(Evaluator::isQualified).count()
    ));
    setRecipeViolations(new Counter(
        metrics.stream().map(Metrics::getRecipeViolations).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getRecipeViolations).filter(Evaluator::isQualified).count()
    ));
    setTest(new Counter(
        metrics.stream().map(Metrics::getTest).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getTest).filter(Evaluator::isQualified).count()
    ));
    testedRecipes = new Counter(metrics.size(), getTest().getDenominator());
  }
}
