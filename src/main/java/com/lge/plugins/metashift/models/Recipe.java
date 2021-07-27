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

import com.lge.plugins.metashift.models.factory.CodeSizeFactory;
import com.lge.plugins.metashift.models.factory.CodeViolationFactory;
import com.lge.plugins.metashift.models.factory.CommentFactory;
import com.lge.plugins.metashift.models.factory.ComplexityFactory;
import com.lge.plugins.metashift.models.factory.CoverageFactory;
import com.lge.plugins.metashift.models.factory.DuplicationFactory;
import com.lge.plugins.metashift.models.factory.MutationTestFactory;
import com.lge.plugins.metashift.models.factory.PremirrorCacheFactory;
import com.lge.plugins.metashift.models.factory.RecipeSizeFactory;
import com.lge.plugins.metashift.models.factory.RecipeViolationFactory;
import com.lge.plugins.metashift.models.factory.SharedStateCacheFactory;
import com.lge.plugins.metashift.models.factory.TestFactory;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import hudson.FilePath;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a recipe containing various data objects for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe extends Data implements Streamable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -3212169684403928336L;

  /**
   * Represents the data list.
   */
  private final DataList dataList;

  /**
   * Represents the functional interface of factory methods.
   *
   * @param <T> first input type
   * @param <V> second input type
   */
  @FunctionalInterface
  private interface FactoryFunction<T, V> {

    void apply(T t, V v) throws IOException, InterruptedException;
  }

  /**
   * Creates new task to trigger a given factory method.
   *
   * @param functor to parse the file
   * @param path    to the report directory
   * @return task object
   */
  private Callable<Void> newTask(FactoryFunction<FilePath, DataList> functor, FilePath path,
      DataList dataList) {
    return () -> {
      functor.apply(path, dataList);
      return null;
    };
  }

  /**
   * Creates a Recipe object using the given recipe directory.
   *
   * @param path to the recipe directory
   * @throws IllegalArgumentException if the recipe name is malformed or the path is invalid
   * @throws InterruptedException     if an interruption occurs
   * @throws IOException              if a file IO fails
   */
  public Recipe(final FilePath path)
      throws IllegalArgumentException, InterruptedException, IOException {
    this(path.getName());
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    ExecutorServiceUtils.invokeAll(
        newTask(CodeSizeFactory::create, path, dataList),
        newTask(CodeViolationFactory::create, path, dataList),
        newTask(CommentFactory::create, path, dataList),
        newTask(ComplexityFactory::create, path, dataList),
        newTask(CoverageFactory::create, path, dataList),
        newTask(DuplicationFactory::create, path, dataList),
        newTask(MutationTestFactory::create, path, dataList),
        newTask(PremirrorCacheFactory::create, path, dataList),
        newTask(RecipeSizeFactory::create, path, dataList),
        newTask(RecipeViolationFactory::create, path, dataList),
        newTask(SharedStateCacheFactory::create, path, dataList),
        newTask(TestFactory::create, path, dataList));
  }

  /**
   * Creates an empty Recipe object with the given recipe name.
   *
   * @param recipe name
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final String recipe) throws IllegalArgumentException {
    super(recipe);
    dataList = new DataList();
  }

  /**
   * Adds the given object to the list.
   *
   * @param object to add
   * @param <T>    object type
   */
  public <T> void add(final T object) {
    dataList.add(object);
  }

  @Override
  public <T> boolean isAvailable(final Class<T> clazz) {
    return dataList.isAvailable(clazz);
  }

  @Override
  public <T> Stream<T> objects(final Class<T> clazz) {
    return dataList.objects(clazz);
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
    return new EqualsBuilder()
        .append(getRecipe(), other.getRecipe())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getRecipe())
        .toHashCode();
  }
}
