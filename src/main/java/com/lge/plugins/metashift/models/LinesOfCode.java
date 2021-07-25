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

/**
 * LinesOfCode class.
 *
 * @author Sung Gon Kim
 */
public class LinesOfCode implements Serializable {

  private static final long serialVersionUID = -1196699667444485722L;

  private ValueWithDifference<Long> lines;
  private ValueWithDifference<Long> functions;
  private ValueWithDifference<Long> classes;
  private ValueWithDifference<Long> files;
  private ValueWithDifference<Long> recipes;

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
    this.lines = new ValueWithDifference<>(lines, 0L);
    this.functions = new ValueWithDifference<>(functions, 0L);
    this.classes = new ValueWithDifference<>(classes, 0L);
    this.files = new ValueWithDifference<>(files, 0L);
    this.recipes = new ValueWithDifference<>(recipes, 0L);
  }

  /**
   * Default constructor.
   *
   * @param lines     the number of lines
   * @param functions the number of functions
   * @param classes   the number of classes
   * @param files     the number of files
   */
  public LinesOfCode(long lines, long functions, long classes, long files) {
    this(lines, functions, classes, files, 0L);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public LinesOfCode(LinesOfCode other) {
    this.lines = new ValueWithDifference<>(other.lines);
    this.functions = new ValueWithDifference<>(other.functions);
    this.classes = new ValueWithDifference<>(other.classes);
    this.files = new ValueWithDifference<>(other.files);
    this.recipes = new ValueWithDifference<>(other.recipes);
  }

  /**
   * Returns the lines of code and the difference values.
   *
   * @return the lines of code value
   */
  public ValueWithDifference<Long> getLines() {
    return lines;
  }

  /**
   * Returns the number of functions and the difference values.
   *
   * @return the number of function value
   */
  public ValueWithDifference<Long> getFunctions() {
    return functions;
  }

  /**
   * Returns the number of classes and the difference values.
   *
   * @return the number of classes
   */
  public ValueWithDifference<Long> getClasses() {
    return classes;
  }

  /**
   * Returns the number of files and the difference values.
   *
   * @return the number of file value
   */
  public ValueWithDifference<Long> getFiles() {
    return files;
  }

  /**
   * Returns the number of recipes and the difference values.
   *
   * @return the number of recipe value
   */
  public ValueWithDifference<Long> getRecipes() {
    return recipes;
  }

  /**
   * Sets the difference values.
   *
   * @param other object
   */
  public void setDifference(LinesOfCode other) {
    long lines = getLines().getValue();
    long linesDiff = lines - other.getLines().getValue();
    this.lines = new ValueWithDifference<>(lines, linesDiff);

    long functions = getFunctions().getValue();
    long functionsDiff = functions - other.getFunctions().getValue();
    this.functions = new ValueWithDifference<>(functions, functionsDiff);

    long classes = getClasses().getValue();
    long classesDiff = classes - other.getClasses().getValue();
    this.classes = new ValueWithDifference<>(classes, classesDiff);

    long files = getFiles().getValue();
    long filesDiff = files - other.getFiles().getValue();
    this.files = new ValueWithDifference<>(files, filesDiff);

    long recipes = getRecipes().getValue();
    long recipesDiff = recipes - other.getRecipes().getValue();
    this.recipes = new ValueWithDifference<>(recipes, recipesDiff);
  }
}
