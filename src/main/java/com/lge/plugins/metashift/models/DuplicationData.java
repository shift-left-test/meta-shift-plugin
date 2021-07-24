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
 * Represents the duplication data.
 *
 * @author Sung Gon Kim
 */
public final class DuplicationData extends Data {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 5396591078130330879L;

  /**
   * Represents the name of the file.
   */
  private final String file;

  /**
   * Represents the number of lines.
   */
  private final long lines;

  /**
   * Represents the number of duplicated lines.
   */
  private final long duplicatedLines;

  /**
   * Default constructor.
   *
   * @param recipe          name
   * @param file            name
   * @param lines           the number of lines
   * @param duplicatedLines the number of duplicated lines
   */
  public DuplicationData(final String recipe, final String file,
      final long lines, final long duplicatedLines) {
    super(recipe);
    this.file = file;
    this.lines = lines;
    this.duplicatedLines = duplicatedLines;
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
    DuplicationData other = (DuplicationData) object;
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
   * Returns the name of the file.
   *
   * @return file name
   */
  public String getFile() {
    return file;
  }

  /**
   * Returns the number of lines.
   *
   * @return the number of lines
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the number of duplicated lines.
   *
   * @return the number of duplicated lines
   */
  public long getDuplicatedLines() {
    return duplicatedLines;
  }
}
