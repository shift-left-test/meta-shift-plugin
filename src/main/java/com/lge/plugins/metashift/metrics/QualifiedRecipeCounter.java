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
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Counts the number of qualified recipes based on the given criteria.
 *
 * @author Sung Gon Kim
 */
public final class QualifiedRecipeCounter implements Queryable<Counter> {

  /**
   * Represents the counter types.
   */
  private enum Type {
    /**
     * Recipes counter.
     */
    RECIPES,

    /**
     * Cache availability counter.
     */
    CACHE_AVAILABILITY,

    /**
     * Code violation counter.
     */
    CODE_VIOLATIONS,

    /**
     * Comment counter.
     */
    COMMENTS,

    /**
     * Complexity counter.
     */
    COMPLEXITY,

    /**
     * Coverage counter.
     */
    COVERAGE,

    /**
     * Duplication counter.
     */
    DUPLICATIONS,

    /**
     * Mutation test counter.
     */
    MUTATION_TEST,

    /**
     * Recipe violation counter.
     */
    RECIPE_VIOLATIONS,

    /**
     * Test counter.
     */
    TEST,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, Counter> collection;

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
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
    this.criteria = criteria;
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

  /**
   * Parses the given recipes to count the number of qualified recipes.
   *
   * @param recipes to parse
   * @return self object
   */
  public QualifiedRecipeCounter parse(final Recipes recipes) {
    collection.put(Type.RECIPES, countBy(recipes, new Metrics(criteria)));
    collection.put(Type.CACHE_AVAILABILITY, countBy(recipes, new CacheEvaluator(criteria)));
    collection.put(Type.CODE_VIOLATIONS, countBy(recipes, new CodeViolationEvaluator(criteria)));
    collection.put(Type.COMMENTS, countBy(recipes, new CommentEvaluator(criteria)));
    collection.put(Type.COMPLEXITY, countBy(recipes, new ComplexityEvaluator(criteria)));
    collection.put(Type.COVERAGE, countBy(recipes, new CoverageEvaluator(criteria)));
    collection.put(Type.DUPLICATIONS, countBy(recipes, new DuplicationEvaluator(criteria)));
    collection.put(Type.MUTATION_TEST, countBy(recipes, new MutationTestEvaluator(criteria)));
    collection.put(Type.RECIPE_VIOLATIONS,
        countBy(recipes, new RecipeViolationEvaluator(criteria)));
    collection.put(Type.TEST, countBy(recipes, new TestEvaluator(criteria)));
    return this;
  }

  /**
   * Returns the counter of qualified recipes.
   *
   * @return counter object
   */
  public Counter getRecipes() {
    return collection.get(Type.RECIPES);
  }

  @Override
  public Counter getCacheAvailability() {
    return collection.get(Type.CACHE_AVAILABILITY);
  }

  @Override
  public Counter getCodeViolations() {
    return collection.get(Type.CODE_VIOLATIONS);
  }

  @Override
  public Counter getComments() {
    return collection.get(Type.COMMENTS);
  }

  @Override
  public Counter getComplexity() {
    return collection.get(Type.COMPLEXITY);
  }

  @Override
  public Counter getCoverage() {
    return collection.get(Type.COVERAGE);
  }

  @Override
  public Counter getDuplications() {
    return collection.get(Type.DUPLICATIONS);
  }

  @Override
  public Counter getMutationTest() {
    return collection.get(Type.MUTATION_TEST);
  }

  @Override
  public Counter getRecipeViolations() {
    return collection.get(Type.RECIPE_VIOLATIONS);
  }

  @Override
  public Counter getTest() {
    return collection.get(Type.TEST);
  }
}
