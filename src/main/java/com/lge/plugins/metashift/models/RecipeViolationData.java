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

/**
 * Represents the recipe violation data.
 *
 * @author Sung Gon Kim
 */
public abstract class RecipeViolationData implements Data<RecipeViolationData> {
  /**
   * Represents the name of the recipe.
   */
  private String recipe;
  /**
   * Represents the name of the file.
   */
  private String file;
  /**
   * Represents the line number.
   */
  private int line;
  /**
   * Represents the violation rule.
   */
  private String rule;
  /**
   * Represents the description of the violation.
   */
  private String description;
  /**
   * Represents the severity of the violation.
   */
  private String severity;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param file name
   * @param line number
   * @param rule name
   * @param description of the recipe violation
   * @param severity of the recipe violation
   */
  public RecipeViolationData(final String recipe, final String file,
                             final int line, final String rule,
                             final String description,
                             final String severity) {
    this.recipe = recipe;
    this.file = file;
    this.line = line;
    this.rule = rule;
    this.description = description;
    this.severity = severity;
  }

  @Override
  public final int compareTo(final RecipeViolationData other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    compared = file.compareTo(other.file);
    if (compared != 0) {
      return compared;
    }
    compared = Integer.compare(line, other.line);
    if (compared != 0) {
      return compared;
    }
    compared = rule.compareTo(other.rule);
    if (compared != 0) {
      return compared;
    }
    return 0;
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    RecipeViolationData other = (RecipeViolationData) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (!file.equals(other.file)) {
      return false;
    }
    if (line != other.line) {
      return false;
    }
    if (!rule.equals(other.rule)) {
      return false;
    }
    return true;
  }

  @Override
  public final String getRecipe() {
    return recipe;
  }

  /**
   * Return the file of the recipe violation.
   *
   * @return file
   */
  public final String getFile() {
    return file;
  }

  /**
   * Return the line number of the recipe violation.
   *
   * @return line number
   */
  public final int getLine() {
    return line;
  }

  /**
   * Return the rule of the recipe violation.
   *
   * @return rule
   */
  public final String getRule() {
    return rule;
  }

  /**
   * Return the description of the recipe violation.
   *
   * @return description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Return the severity of the recipe violation.
   *
   * @return severity
   */
  public final String getSeverity() {
    return severity;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + file.hashCode();
    hashCode = prime * hashCode + line;
    hashCode = prime * hashCode + rule.hashCode();
    return hashCode;
  }
}
