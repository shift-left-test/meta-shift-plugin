/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the lines of code of recipes.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeData extends Data {

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
    RecipeSizeData other = (RecipeSizeData) object;
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
}
