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
public abstract class RecipeViolationData extends ViolationData<RecipeViolationData> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -5849334727328868160L;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param rule        name
   * @param description of the recipe violation
   * @param severity    of the recipe violation
   * @param level       of the recipe violation
   */
  public RecipeViolationData(final String recipe, final String file,
      final long line, final String rule, final String description,
      final String severity, final String level) {
    super(recipe, file, line, rule, description, severity, level);
  }

  @Override
  public final int compareTo(final RecipeViolationData other) {
    return compareEach(
        getRecipe().compareTo(other.getRecipe()),
        getFile().compareTo(other.getFile()),
        Long.compare(getLine(), other.getLine()),
        getRule().compareTo(other.getRule())
    );
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
    return compareTo((RecipeViolationData) object) == 0;
  }

  @Override
  public final int hashCode() {
    return computeHashCode(getClass(), getRecipe(), getFile(), getLine(), getRule());
  }
}
