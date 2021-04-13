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

import com.lge.plugins.metashift.models.factory.CacheFactory;
import com.lge.plugins.metashift.models.factory.CodeSizeFactory;
import com.lge.plugins.metashift.models.factory.CodeViolationFactory;
import com.lge.plugins.metashift.models.factory.CommentFactory;
import com.lge.plugins.metashift.models.factory.ComplexityFactory;
import com.lge.plugins.metashift.models.factory.CoverageFactory;
import com.lge.plugins.metashift.models.factory.DuplicationFactory;
import com.lge.plugins.metashift.models.factory.MutationTestFactory;
import com.lge.plugins.metashift.models.factory.RecipeViolationFactory;
import com.lge.plugins.metashift.models.factory.TestFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents a recipe containing various data for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe extends Data<Recipe> implements Collectable {

  /**
   * Represents the heterogeneous data list.
   */
  private final List<Object> objects;

  /**
   * Default constructor.
   *
   * @param path to recipe directory
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final File path) throws IllegalArgumentException {
    this(path.getName());
  }

  /**
   * Default constructor.
   *
   * @param recipe name
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final String recipe) throws IllegalArgumentException {
    super(recipe);
    String regexp = "^(?<recipe>[\\w-+]+)-(?<version>[\\w.+]+)-(?<revision>[\\w.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(recipe);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid recipe name: " + recipe);
    }
    objects = new ArrayList<>();
  }

  /**
   * Create a Recipe object using the given recipe directory.
   *
   * @param path to a recipe directory
   * @throws IllegalArgumentException if the path is invalid
   */
  public static Recipe create(final File path) throws IllegalArgumentException {
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    Recipe recipe = new Recipe(path);
    recipe.addAll(CacheFactory.create(path));
    recipe.addAll(CodeSizeFactory.create(path));
    recipe.addAll(CodeViolationFactory.create(path));
    recipe.addAll(CommentFactory.create(path));
    recipe.addAll(ComplexityFactory.create(path));
    recipe.addAll(CoverageFactory.create(path));
    recipe.addAll(DuplicationFactory.create(path));
    recipe.addAll(MutationTestFactory.create(path));
    recipe.addAll(RecipeViolationFactory.create(path));
    recipe.addAll(TestFactory.create(path));
    return recipe;
  }

  /**
   * Adds the given object to the collection.
   *
   * @param object to add
   */
  public void add(final Object object) {
    this.objects.add(object);
  }

  /**
   * Adds the given objects to the collection.
   *
   * @param objects to add
   */
  public void addAll(final List<? extends Data<?>> objects) {
    this.objects.addAll(objects);
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  @Override
  public final <T> Stream<T> objects(final Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }

  @Override
  public int compareTo(final Recipe other) {
    return compareEach(getRecipe().compareTo(other.getRecipe()));
  }

  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    return compareTo((Recipe) object) == 0;
  }

  @Override
  public int hashCode() {
    return computeHashCode(getClass(), getRecipe());
  }
}
