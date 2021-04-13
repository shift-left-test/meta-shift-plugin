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

import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Collects the qualified recipes based on the given metrics.
 *
 * @author Sung Gon Kim
 */
public final class QualifiedRecipes implements Queryable<Recipes> {

  /**
   * Represents the recipes used for collection.
   */
  private final Recipes recipes;

  /**
   * Represents the metrics used for collection.
   */
  private final Metrics metrics;

  /**
   * Default constructor.
   *
   * @param recipes for collection
   * @param metrics for collection
   */
  public QualifiedRecipes(final Recipes recipes, final Metrics metrics) {
    this.metrics = metrics;
    this.recipes = recipes;
  }

  /**
   * Returns the filtered list of recipes.
   *
   * @param predicate to filter
   * @return list of recipes
   */
  private Collection<Recipe> filteredBy(final Predicate<? super Recipe> predicate) {
    return recipes.stream().filter(predicate).collect(Collectors.toList());
  }

  /**
   * Returns the original recipes.
   *
   * @return list of recipes
   */
  public Recipes getOriginal() {
    return recipes;
  }

  /**
   * Returns the list of qualified recipes.
   *
   * @return list of recipes
   */
  public Recipes getQualified() {
    return new Recipes(filteredBy(o -> metrics.parse(o).isQualified()));
  }

  @Override
  public Recipes getCacheAvailability() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getCacheAvailability().isQualified()));
  }

  @Override
  public Recipes getCodeViolations() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getCodeViolations().isQualified()));
  }

  @Override
  public Recipes getComments() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getComments().isQualified()));
  }

  @Override
  public Recipes getComplexity() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getComplexity().isQualified()));
  }

  @Override
  public Recipes getCoverage() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getCoverage().isQualified()));
  }

  @Override
  public Recipes getDuplications() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getDuplications().isQualified()));
  }

  @Override
  public Recipes getMutationTest() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getMutationTest().isQualified()));
  }

  @Override
  public Recipes getRecipeViolations() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getRecipeViolations().isQualified()));
  }

  @Override
  public Recipes getTest() {
    return new Recipes(filteredBy(o -> metrics.parse(o).getTest().isQualified()));
  }
}
