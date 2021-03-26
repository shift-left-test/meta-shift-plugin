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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a recipe containing various data for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe implements Data<Recipe>, Acceptor {
  /**
   * Represents the name of the recipe.
   */
  private String recipe;
  /**
   * Represents the data set mapper.
   */
  private Map<Class<?>, DataList<?>> collection;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final String recipe) {
    String regexp = "^(?<recipe>[\\w-+]+)(?:-)(?<version>[\\w.+]+)(?:-)(?<revision>[\\w.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(recipe);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid recipe name: " + recipe);
    }

    this.recipe = recipe;

    collection = new HashMap<>();
    collection.put(CacheList.class, new CacheList());
    collection.put(RecipeViolationList.class, new RecipeViolationList());
    collection.put(CommentList.class, new CommentList());
    collection.put(CodeViolationList.class, new CodeViolationList());
    collection.put(ComplexityList.class, new ComplexityList());
    collection.put(DuplicationList.class, new DuplicationList());
    collection.put(TestList.class, new TestList());
    // TODO(sunggon82.kim): CoverageList required
    // collection.put(CoverageList.class, new CoverageList());
    collection.put(MutationTestList.class, new MutationTestList());
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
   * Set the given container object to the collection.
   *
   * @param <T> the class type
   * @param object to store
   */
  public <T extends DataList<?>> void set(final T object) {
    collection.put(object.getClass(), object);
  }

  /**
   * Return the relevant container object from the collection.
   *
   * @param <T> the class type
   * @param clazz the name of the class
   * @return container object
   */
  public <T extends DataList<?>> T collection(final Class<T> clazz) {
    return clazz.cast(collection.get(clazz));
  }

  @Override
  public void accept(final Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int compareTo(final Recipe other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    return 0;
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
    Recipe other = (Recipe) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    return true;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    return hashCode;
  }
}
