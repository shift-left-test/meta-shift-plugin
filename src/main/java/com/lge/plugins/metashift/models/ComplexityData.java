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
 * Represents the complexity data.
 *
 * @author Sung Gon Kim
 */
public final class ComplexityData extends Data {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 919490239643007829L;

  /**
   * Represents the name of the file.
   */
  private final String file;

  /**
   * Represents the name of the function.
   */
  private final String function;

  /**
   * Represents the start line of the region.
   */
  private final long start;

  /**
   * Represents the end line of the region.
   */
  private final long end;

  /**
   * Represents the complexity value.
   */
  private final long value;

  /**
   * Default constructor.
   *
   * @param recipe   name
   * @param file     name
   * @param function name
   * @param start    line
   * @param end      line
   * @param value    of the complexity
   */
  public ComplexityData(final String recipe, final String file, final String function,
      final long start, final long end, final long value) {
    super(recipe);
    this.file = file;
    this.function = function;
    this.start = start;
    this.end = end;
    this.value = value;
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
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getFunction(), other.getFunction())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getFunction())
        .toHashCode();
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
   * Returns the start line of the region.
   *
   * @return start line
   */
  public long getStart() {
    return start;
  }

  /**
   * Returns the end line of the region.
   *
   * @return end line
   */
  public long getEnd() {
    return end;
  }

  /**
   * Returns the complexity value.
   *
   * @return complexity value
   */
  public long getValue() {
    return value;
  }
}
