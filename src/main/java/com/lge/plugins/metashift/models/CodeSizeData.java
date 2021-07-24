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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the code size data.
 *
 * @author Sung Gon Kim
 */
public final class CodeSizeData extends Data {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 6104523429583412555L;

  /**
   * Represents the filename.
   */
  private final String file;

  /**
   * Represents the number of lines for the file.
   */
  private final long lines;

  /**
   * Represents the number of functions for the file.
   */
  private final long functions;

  /**
   * Represents the number of classes for the file.
   */
  private final long classes;

  /**
   * Default constructor.
   *
   * @param recipe    name.
   * @param file      name.
   * @param lines     the number of lines for the file.
   * @param functions the number of functions for the file.
   * @param classes   the number of classes for the file.
   */
  public CodeSizeData(final String recipe, final String file, final long lines,
      final long functions, final long classes) {
    super(recipe);
    this.file = file;
    this.lines = lines;
    this.functions = functions;
    this.classes = classes;
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
    CodeSizeData other = (CodeSizeData) object;
    return new EqualsBuilder()
        .append(getRecipe(), other.getRecipe())
        .append(getFile(), other.getFile())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getRecipe())
        .append(getFile())
        .toHashCode();
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

  /**
   * Return the number of functions.
   *
   * @return functions
   */
  public long getFunctions() {
    return functions;
  }

  /**
   * Return the number of classes.
   *
   * @return classes
   */
  public long getClasses() {
    return classes;
  }
}
