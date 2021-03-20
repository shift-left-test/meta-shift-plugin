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
 * Represents the complexity data.
 *
 * @author Sung Gon Kim
 */
public final class ComplexityData implements Data<ComplexityData> {
  /**
   * Represents the name of the recipe.
   */
  private String recipe;
  /**
   * Represents the name of the file.
   */
  private String file;
  /**
   * Represents the name of the funcion.
   */
  private String function;
  /**
   * Represents the complexity value.
   */
  private int value;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param file name
   * @param function name
   * @param value of the complexity
   */
  public ComplexityData(final String recipe, final String file, final String function,
                        final int value) {
    this.recipe = recipe;
    this.file = file;
    this.function = function;
    this.value = value;
  }

  @Override
  public int compareTo(final ComplexityData other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    compared = file.compareTo(other.file);
    if (compared != 0) {
      return compared;
    }
    compared = function.compareTo(other.function);
    if (compared != 0) {
      return compared;
    }
    return 0;
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
    ComplexityData other = (ComplexityData) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (!file.equals(other.file)) {
      return false;
    }
    if (!function.equals(other.function)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + file.hashCode();
    hashCode = prime * hashCode + function.hashCode();
    return hashCode;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  /**
   * Returns the file name.
   *
   * @return filename
   */
  public String getFile() {
    return file;
  }

  /**
   * Returns the function name.
   *
   * @return function name
   */
  public String getFunction() {
    return function;
  }

  /**
   * Returns the complexity value.
   *
   * @return complexity value
   */
  public int getValue() {
    return value;
  }
}