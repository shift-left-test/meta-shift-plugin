/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
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
