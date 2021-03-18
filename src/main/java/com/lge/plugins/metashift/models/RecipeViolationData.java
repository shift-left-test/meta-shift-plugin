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
 * Represents the recipe violation data
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationData implements Data<RecipeViolationData> {
  private String recipe;
  private String file;
  private int line;
  private String rule;
  private String description;
  private String severity;

  /**
   * Default constructor
   *
   * @param recipe name
   * @param file name
   * @param line number
   * @param rule name
   * @param description of the recipe violation
   * @param severity of the recipe violation
   */
  public RecipeViolationData(String recipe, String file, int line, String rule,
                             String description, String severity) {
    this.recipe = recipe;
    this.file = file;
    this.line = line;
    this.rule = rule;
    this.description = description;
    this.severity = severity;
  }

  @Override
  public int compareTo(RecipeViolationData other) {
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
    return 0;
  }

  @Override
  public boolean equals(Object object) {
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
    return true;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  /**
   * Return the file of the recipe violation
   *
   * @return file
   */
  public String getFile() {
    return file;
  }

  /**
   * Return the line number of the recipe violation
   *
   * @return line number
   */
  public int getLine() {
    return line;
  }

  /**
   * Return the rule of the recipe violation
   *
   * @return rule
   */
  public String getRule() {
    return rule;
  }

  /**
   * Return the description of the recipe violation
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Return the severity of the recipe violation
   *
   * @return severity
   */
  public String getSeverity() {
    return severity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + file.hashCode();
    hashCode = prime * hashCode + line;
    return hashCode;
  }
}
