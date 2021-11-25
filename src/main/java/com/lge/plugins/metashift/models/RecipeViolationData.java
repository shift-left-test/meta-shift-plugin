/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the recipe violation data.
 *
 * @author Sung Gon Kim
 */
public abstract class RecipeViolationData extends ViolationData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -5849334727328868160L;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param rule        name
   * @param description of the recipe violation
   * @param severity    of the recipe violation
   * @param level       of the recipe violation
   */
  public RecipeViolationData(final String recipe, final String file,
      final long line, final String rule, final String description,
      final String severity, final Level level) {
    super(recipe, file, line, rule, description, severity, level);
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    RecipeViolationData other = (RecipeViolationData) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getLine(), other.getLine())
        .append(getRule(), other.getRule())
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getLine())
        .append(getRule())
        .toHashCode();
  }
}
