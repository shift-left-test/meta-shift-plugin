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
 * Represents the coveage data.
 *
 * @author Sung Gon Kim
 */
public abstract class CoverageData extends Data<CoverageData> {

  /**
   * Represents the file name.
   */
  private final String file;

  /**
   * Represents the function name.
   */
  private final String function;

  /**
   * Represents the line number.
   */
  private final int line;

  /**
   * Represents the coverage item index.
   */
  private final int index;

  /**
   * Represents the coverage status.
   */
  private final boolean covered;

  /**
   * Default constructor.
   *
   * @param recipe   name
   * @param file     name
   * @param function name
   * @param line     number
   * @param index    coverage item index
   * @param covered  coverage status
   */
  public CoverageData(final String recipe, final String file, final String function, final int line,
      final int index, final boolean covered) {
    super(recipe);
    this.file = file;
    this.function = function;
    this.line = line;
    this.index = index;
    this.covered = covered;
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
    CoverageData other = (CoverageData) object;
    if (!getRecipe().equals(other.getRecipe())) {
      return false;
    }
    if (!file.equals(other.file)) {
      return false;
    }
    if (!function.equals(other.function)) {
      return false;
    }
    if (line != other.line) {
      return false;
    }
    return index == other.index;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + getRecipe().hashCode();
    hashCode = prime * hashCode + file.hashCode();
    hashCode = prime * hashCode + function.hashCode();
    hashCode = prime * hashCode + line;
    hashCode = prime * hashCode + index;
    return hashCode;
  }

  @Override
  public int compareTo(final CoverageData other) {
    int compared;
    compared = getRecipe().compareTo(other.getRecipe());
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
    compared = Integer.compare(line, other.line);
    if (compared != 0) {
      return compared;
    }
    return Integer.compare(index, other.index);
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
   * Returns the line number.
   *
   * @return line number
   */
  public int getLine() {
    return line;
  }

  /**
   * Returns the coverage item index.
   *
   * @return coverage item index
   */
  public int getIndex() {
    return index;
  }

  /**
   * Represents the coverage status.
   *
   * @return true if it is covered, false otherwise
   */
  public boolean isCovered() {
    return covered;
  }
}
