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

/**
 * CodeSizeDelta class.
 *
 * @author Sung Gon Kim
 */
public final class CodeSizeDelta {

  /**
   * Represents the delta of the recipes.
   */
  private final long recipes;

  /**
   * Represents the delta of the lines.
   */
  private final long files;

  /**
   * Represents the delta of the lines.
   */
  private final long lines;

  /**
   * Represents the delta of the functions.
   */
  private final long functions;

  /**
   * Represents the delta of the classes.
   */
  private final long classes;

  /**
   * Default constructor.
   */
  public CodeSizeDelta() {
    this(0, 0, 0, 0, 0);
  }

  /**
   * Default constructor.
   *
   * @param recipes   the delta of the recipes
   * @param files     the delta of the files
   * @param lines     the delta of the lines
   * @param functions the delta of the functions
   * @param classes   the delta of the classes
   */
  public CodeSizeDelta(final long recipes, final long files, final long lines, final long functions,
      final long classes) {
    this.recipes = recipes;
    this.files = files;
    this.lines = lines;
    this.functions = functions;
    this.classes = classes;
  }

  /**
   * Returns the delta of the recipes.
   *
   * @return recipe delta
   */
  public long getRecipes() {
    return recipes;
  }

  /**
   * Returns the delta of the files.
   *
   * @return file delta
   */
  public long getFiles() {
    return files;
  }

  /**
   * Returns the delta of the lines.
   *
   * @return line delta
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the delta of the functions.
   *
   * @return function delta
   */
  public long getFunctions() {
    return functions;
  }

  /**
   * Returns the delta of the classes.
   *
   * @return class delta
   */
  public long getClasses() {
    return classes;
  }

  /**
   * Creates the delta between the two CodeSizeEvaluator objects.
   *
   * @param first  CodeSizeEvaluator object
   * @param second CodeSizeEvaluator object
   * @return the delta between the two objects
   */
  public static CodeSizeDelta between(final CodeSizeEvaluator first,
      final CodeSizeEvaluator second) {
    CodeSizeEvaluator former = (first != null) ? first : new CodeSizeEvaluator();
    CodeSizeEvaluator latter = (second != null) ? second : new CodeSizeEvaluator();
    return new CodeSizeDelta(
        latter.getRecipes() - former.getRecipes(),
        latter.getFiles() - former.getFiles(),
        latter.getLines() - former.getLines(),
        latter.getFunctions() - former.getFunctions(),
        latter.getClasses() - former.getClasses()
    );
  }
}
