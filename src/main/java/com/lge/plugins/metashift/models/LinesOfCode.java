/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
