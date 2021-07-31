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

import com.lge.plugins.metashift.parsers.CodeSizeParser;
import com.lge.plugins.metashift.parsers.CodeViolationParser;
import com.lge.plugins.metashift.parsers.CommentParser;
import com.lge.plugins.metashift.parsers.ComplexityParser;
import com.lge.plugins.metashift.parsers.CoverageParser;
import com.lge.plugins.metashift.parsers.DuplicationParser;
import com.lge.plugins.metashift.parsers.MutationTestParser;
import com.lge.plugins.metashift.parsers.PremirrorCacheParser;
import com.lge.plugins.metashift.parsers.RecipeSizeParser;
import com.lge.plugins.metashift.parsers.RecipeViolationParser;
import com.lge.plugins.metashift.parsers.SharedStateCacheParser;
import com.lge.plugins.metashift.parsers.TestParser;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import hudson.FilePath;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
   * Creates a Recipe object using the given recipe directory.
   *
   * @param path to the recipe directory
   * @throws IllegalArgumentException if the recipe name is malformed or the path is invalid
   * @throws IOException              if a file IO fails
   * @throws InterruptedException     if an interruption occurs
   */
  public Recipe(final FilePath path)
      throws IllegalArgumentException, IOException, InterruptedException {
    this(path.getName());
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    ExecutorServiceUtils.invokeAll(
        new CodeSizeParser(path, dataList),
        new CodeViolationParser(path, dataList),
        new CommentParser(path, dataList),
        new ComplexityParser(path, dataList),
        new CoverageParser(path, dataList),
        new DuplicationParser(path, dataList),
        new MutationTestParser(path, dataList),
        new PremirrorCacheParser(path, dataList),
        new RecipeSizeParser(path, dataList),
        new RecipeViolationParser(path, dataList),
        new SharedStateCacheParser(path, dataList),
        new TestParser(path, dataList)
    );
  }

  /**
   * Creates an empty Recipe object with the given recipe name.
   *
   * @param name of the recipe
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Recipe(final String name) throws IllegalArgumentException {
    super(name);
    String regexp = "^(?<recipe>[\\w-.+]+)-(?<version>[\\w-.+]+)-(?<revision>[\\w-.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid recipe name: " + name);
    }
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
  public <T> boolean contains(final Class<T> clazz) {
    return dataList.contains(clazz);
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
        .append(getName(), other.getName())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .toHashCode();
  }
}
