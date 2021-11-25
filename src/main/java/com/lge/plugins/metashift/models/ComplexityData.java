/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
