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
 * Represents the lines of code of recipes.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeData extends Data<RecipeSizeData> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7242519516364532523L;

  /**
   * Represents the filename.
   */
  private final String file;

  /**
   * Represents the number of lines for the file.
   */
  private final long lines;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param file   name
   * @param lines  the number of the lines
   */
  public RecipeSizeData(final String recipe, final String file, final long lines) {
    super(recipe);
    this.file = file;
    this.lines = lines;
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
    return compareTo((RecipeSizeData) object) == 0;
  }

  @Override
  public int hashCode() {
    return computeHashCode(getClass(), getRecipe(), file);
  }

  @Override
  public int compareTo(final RecipeSizeData other) {
    return compareEach(
        getRecipe().compareTo(other.getRecipe()),
        file.compareTo(other.file)
    );
  }

  /**
   * Return the filename.
   *
   * @return filename
   */
  public String getFile() {
    return file;
  }

  /**
   * Return the number of lines.
   *
   * @return lines
   */
  public long getLines() {
    return lines;
  }
}
