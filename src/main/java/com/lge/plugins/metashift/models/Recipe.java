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

import com.lge.plugins.metashift.metrics.Visitable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents a recipe containing various data for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe extends Data<Recipe> implements Acceptable, Collectable {

  /**
   * Represents the data set mapper.
   */
  private final Map<Class<?>, DataList<?>> collection;

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

    collection = new HashMap<>();
    collection.put(CacheList.class, new CacheList());
    collection.put(RecipeViolationList.class, new RecipeViolationList());
    collection.put(SizeList.class, new SizeList());
    collection.put(CommentList.class, new CommentList());
    collection.put(CodeViolationList.class, new CodeViolationList());
    collection.put(ComplexityList.class, new ComplexityList());
    collection.put(DuplicationList.class, new DuplicationList());
    collection.put(TestList.class, new TestList());
    collection.put(CoverageList.class, new CoverageList());
    collection.put(MutationTestList.class, new MutationTestList());

    objects = new ArrayList<>();
  }

  /**
   * Create a Recipe object using the given recipe directory.
   *
   * @param path to a recipe directory
   * @throws IllegalArgumentException if the path is invalid
   */
  public static Recipe create(File path) throws IllegalArgumentException {
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    Recipe recipe = new Recipe(path);
    recipe.set(CacheList.create(path));
    recipe.set(RecipeViolationList.create(path));
    recipe.set(SizeList.create(path));
    recipe.set(CommentList.create(path));
    recipe.set(CodeViolationList.create(path));
    recipe.set(ComplexityList.create(path));
    recipe.set(DuplicationList.create(path));
    recipe.set(TestList.create(path));
    recipe.set(CoverageList.create(path));
    recipe.set(MutationTestList.create(path));
    return recipe;
  }

  /**
   * Performs the given action for each entry in this map.
   *
   * @param action The action to be performed for each entry
   */
  public void forEach(final BiConsumer<Class<?>, DataList<?>> action) {
    collection.forEach(action);
  }

  /**
   * Adds the given object to the collection.
   *
   * @param object to store
   */
  public void add(final Object object) {
    objects.add(object);
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  @Override
  public final <T> Stream<T> objects(Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }

  /**
   * Set the given container object to the collection.
   *
   * @param <T>    the class type
   * @param object to store
   */
  public <T extends DataList<?>> void set(final T object) {
    // TODO(sunggon82.kim): Remove this after refactoring
    collection.put(object.getClass(), object);
    objects.addAll(object);
  }

  /**
   * Return the relevant container object from the collection.
   *
   * @param <T>   the class type
   * @param clazz the name of the class
   * @return container object
   */
  public <T extends DataList<?>> T get(final Class<T> clazz) {
    return clazz.cast(collection.get(clazz));
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
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
