/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the coverage data.
 *
 * @author Sung Gon Kim
 */
public abstract class CoverageData extends Data {

  /**
   * Represents the coverage type.
   */
  protected enum Type {
    STATEMENT,
    BRANCH,
  }

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 2663571199454502598L;

  /**
   * Represents the file name.
   */
  private final String file;

  /**
   * Represents the line number.
   */
  private final long line;

  /**
   * Represents the coverage item index.
   */
  private final long index;

  /**
   * Represents the coverage status.
   */
  private final boolean covered;

  /**
   * Represents the coverage type.
   */
  private final Type type;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param file    name
   * @param line    number
   * @param index   coverage item index
   * @param covered coverage status
   * @param type    coverage type
   */
  public CoverageData(final String recipe, final String file, final long line, final long index,
      final boolean covered, final Type type) {
    super(recipe);
    this.file = file;
    this.line = line;
    this.index = index;
    this.covered = covered;
    this.type = type;
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
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getLine(), other.getLine())
        .append(getIndex(), other.getIndex())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getLine())
        .append(getIndex())
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
   * Returns the line number.
   *
   * @return line number
   */
  public long getLine() {
    return line;
  }

  /**
   * Returns the coverage item index.
   *
   * @return coverage item index
   */
  public long getIndex() {
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

  /**
   * Returns the type of the coverage.
   *
   * @return coverage type
   */
  public String getType() {
    return type.name();
  }
}
