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
   * Represents the configuration object.
   */
  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for collection
   */
  public QualifiedRecipeCounter(final Configuration configuration) {
    super(new Counter());
    testedRecipes = new Counter();
    this.configuration = configuration;
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
    setPremirrorCache(countBy(recipes, new PremirrorCacheEvaluator(configuration)));
    setSharedStateCache(countBy(recipes, new SharedStateCacheEvaluator(configuration)));
    setCodeViolations(countBy(recipes, new CodeViolationEvaluator(configuration)));
    setComments(countBy(recipes, new CommentEvaluator(configuration)));
    setComplexity(countBy(recipes, new ComplexityEvaluator(configuration)));
    setStatementCoverage(countBy(recipes, new StatementCoverageEvaluator(configuration)));
    setBranchCoverage(countBy(recipes, new BranchCoverageEvaluator(configuration)));
    setDuplications(countBy(recipes, new DuplicationEvaluator(configuration)));
    setMutationTest(countBy(recipes, new MutationTestEvaluator(configuration)));
    setRecipeViolations(countBy(recipes, new RecipeViolationEvaluator(configuration)));
    setTest(countBy(recipes, new TestEvaluator(configuration)));
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
    setStatementCoverage(new Counter(
        metrics.stream().map(Metrics::getStatementCoverage).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getStatementCoverage).filter(Evaluator::isQualified).count()
    ));
    setBranchCoverage(new Counter(
        metrics.stream().map(Metrics::getBranchCoverage).filter(Evaluator::isAvailable).count(),
        metrics.stream().map(Metrics::getBranchCoverage).filter(Evaluator::isQualified).count()
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
