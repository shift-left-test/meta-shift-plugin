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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.io.output.NullPrintStream;

/**
 * Represents a recipe containing various data for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe extends Data<Recipe> implements Streamable {

  /**
   * Represents the functional interface of factory methods.
   *
   * @param <T> input object type
   * @param <R> output object type
   */
  @FunctionalInterface
  private interface FactoryFunction<T, R> {

    R apply(T t) throws IOException;
  }

  /**
   * Represents the object availability.
   */
  private final Set<Class<?>> classes;

  /**
   * Represents the heterogeneous data list.
   */
  private final List<Object> objects;

  /**
   * Adds the list of objects using the given path and the factory method.
   *
   * @param clazz   object type
   * @param functor to parse the file
   * @param path    to the report directory
   */
  private void addAll(Class<?> clazz, FactoryFunction<File, List<?>> functor, File path) {
    try {
      objects.addAll(functor.apply(path));
      classes.add(clazz);
    } catch (IOException ignored) {
      // ignored
    }
  }

  /**
   * Create a Recipe object using the given recipe directory.
   *
   * @param path to recipe directory
   * @throws IllegalArgumentException if the recipe name is malformed or the path is invalid
   */
  public Recipe(final File path) throws IllegalArgumentException {
    this(path, NullPrintStream.NULL_PRINT_STREAM);
  }

  /**
   * Create a Recipe object using the given recipe directory.
   *
   * @param path   to recipe directory
   * @param logger object
   * @throws IllegalArgumentException if the recipe name is malformed or the path is invalid
   */
  public Recipe(final File path, final PrintStream logger) throws IllegalArgumentException {
    this(path.getName());
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }

    String format = "[Recipe] " + getRecipe() + ": processing the %s data%n";
    logger.printf(format, "cache availability");
    addAll(CacheData.class, CacheFactory::create, path);
    logger.printf(format, "code size");
    addAll(CodeSizeData.class, CodeSizeFactory::create, path);
    logger.printf(format, "code violation");
    addAll(CodeViolationData.class, CodeViolationFactory::create, path);
    logger.printf(format, "comment");
    addAll(CommentData.class, CommentFactory::create, path);
    logger.printf(format, "complexity");
    addAll(ComplexityData.class, ComplexityFactory::create, path);
    logger.printf(format, "coverage");
    addAll(CoverageData.class, CoverageFactory::create, path);
    logger.printf(format, "duplication");
    addAll(DuplicationData.class, DuplicationFactory::create, path);
    logger.printf(format, "mutation test");
    addAll(MutationTestData.class, MutationTestFactory::create, path);
    logger.printf(format, "recipe violation");
    addAll(RecipeViolationData.class, RecipeViolationFactory::create, path);
    logger.printf(format, "test");
    addAll(TestData.class, TestFactory::create, path);
  }

  /**
   * Creates an empty Recipe object with the given recipe name.
   *
   * @param recipe name
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final String recipe) throws IllegalArgumentException {
    super(recipe);
    objects = new ArrayList<>();
    classes = new HashSet<>();
  }

  /**
   * Adds the given object to the collection.
   *
   * @param object to add
   * @param <T>    object type
   */
  public <T> void add(final T object) {
    objects.add(object);
    classes.add(object.getClass());
  }

  @Override
  public <T> boolean isAvailable(final Class<T> clazz) {
    return classes.stream().anyMatch(clazz::isAssignableFrom);
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  @Override
  public <T> Stream<T> objects(final Class<T> clazz) {
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
