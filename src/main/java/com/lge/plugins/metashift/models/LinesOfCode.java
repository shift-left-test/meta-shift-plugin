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

import java.io.Serializable;
import java.util.Optional;

/**
 * LinesOfCode class.
 *
 * @author Sung Gon Kim
 */
public class LinesOfCode implements Serializable {

  private static final long serialVersionUID = -1196699667444485722L;

  private final long lines;
  private final long functions;
  private final long classes;
  private final long files;
  private final long recipes;

  /**
   * Default constructor.
   *
   * @param lines     the number of lines
   * @param functions the number of functions
   * @param classes   the number of classes
   * @param files     the number of files
   * @param recipes   the number of recipes
   */
  public LinesOfCode(long lines, long functions, long classes, long files, long recipes) {
    this.lines = lines;
    this.functions = functions;
    this.classes = classes;
    this.files = files;
    this.recipes = recipes;
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public LinesOfCode(LinesOfCode other) {
    this.lines = other.lines;
    this.functions = other.functions;
    this.classes = other.classes;
    this.files = other.files;
    this.recipes = other.recipes;
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code value
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the number of functions.
   *
   * @return the number of function value
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

  /**
   * Returns the number of files.
   *
   * @return the number of file value
   */
  public long getFiles() {
    return files;
  }

  /**
   * Returns the number of recipes.
   *
   * @return the number of recipe value
   */
  public long getRecipes() {
    return recipes;
  }

  /**
   * Returns the difference between two objects.
   *
   * @param other object
   * @return object with the difference values
   */
  public LinesOfCode getDifference(LinesOfCode other) {
    LinesOfCode o = Optional.ofNullable(other).orElse(new LinesOfCode(0, 0, 0, 0, 0));
    return new LinesOfCode(
        this.lines - o.lines,
        this.functions - o.functions,
        this.classes - o.classes,
        this.files - o.files,
        this.recipes - o.recipes
    );
  }
}
