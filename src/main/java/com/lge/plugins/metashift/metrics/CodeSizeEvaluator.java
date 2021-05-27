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

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CodeSizeEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class CodeSizeEvaluator extends NullEvaluator<CodeSizeEvaluator> {

  /**
   * Represents the number of recipes.
   */
  private long recipes;

  /**
   * Represents the number of files.
   */
  private long files;

  /**
   * Represents the number of lines.
   */
  private long lines;

  /**
   * Represents the number of functions.
   */
  private long functions;

  /**
   * Represents the number of classes.
   */
  private long classes;

  /**
   * Default constructor.
   */
  public CodeSizeEvaluator() {
    super();
    recipes = 0;
    files = 0;
    lines = 0;
    functions = 0;
    classes = 0;
  }

  /**
   * Returns the number of recipes.
   *
   * @return the number of recipes
   */
  public long getRecipes() {
    return recipes;
  }

  /**
   * Returns the number of files.
   *
   * @return the number of files
   */
  public long getFiles() {
    return files;
  }

  /**
   * Returns the number of lines.
   *
   * @return the number of lines
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the number of functions.
   *
   * @return the number of functions
   */
  public long getFunctions() {
    return functions;
  }

  /**
   * Returns the number of classes.
   *
   * @return the number of classes
   */
  public long getClasses() {
    return classes;
  }

  @Override
  protected void parseImpl(final Streamable c) {
    recipes = c.objects(CodeSizeData.class).map(CodeSizeData::getRecipe).distinct().count();
    files = c.objects(CodeSizeData.class).map(CodeSizeData::getFile).distinct().count();
    lines = c.objects(CodeSizeData.class).mapToLong(CodeSizeData::getLines).sum();
    functions = c.objects(CodeSizeData.class).mapToLong(CodeSizeData::getFunctions).sum();
    classes = c.objects(CodeSizeData.class).mapToLong(CodeSizeData::getClasses).sum();

    setAvailable(false);
    setDenominator(0);
    setNumerator(0);
  }
}
