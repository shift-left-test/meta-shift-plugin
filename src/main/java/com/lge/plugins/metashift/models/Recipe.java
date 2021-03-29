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
public final class Recipe extends Data<Recipe> implements Acceptable {

  /**
   * Represents the data set mapper.
   */
  private final Map<Class<?>, DataList<?>> collection;

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

    String regexp = "^(?<recipe>[\\w-+]+)(?:-)(?<version>[\\w.+]+)(?:-)(?<revision>[\\w.+]+)$";
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
    collection.put(MutationTestList.class, new MutationTestList());
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
    recipe.set(SizeList.create(path));
    recipe.set(CacheList.create(path));
    recipe.set(CommentList.create(path));
    recipe.set(DuplicationList.create(path));
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
   * Set the given container object to the collection.
   *
   * @param <T>    the class type
   * @param object to store
   */
  public <T extends DataList<?>> void set(final T object) {
    collection.put(object.getClass(), object);
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
    int compared;
    compared = getRecipe().compareTo(other.getRecipe());
    return compared;
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
    return getRecipe().equals(other.getRecipe());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + getRecipe().hashCode();
    return hashCode;
  }
}
