/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the code violation data.
 *
 * @author Sung Gon Kim
 */
public abstract class CodeViolationData extends ViolationData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -4358435516364346298L;

  /**
   * Represents the column number.
   */
  private final long column;

  /**
   * Represents the message.
   */
  private final String message;

  /**
   * Represents the tool.
   */
  private final String tool;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param column      number
   * @param rule        name
   * @param message     of the violation
   * @param description of the violation
   * @param severity    of the violation
   * @param tool        used for analysis
   * @param level       of the violation
   */
  public CodeViolationData(final String recipe, final String file,
      final long line, final long column,
      final String rule, final String message,
      final String description,
      final String severity, final String tool, final Level level) {
    super(recipe, file, line, rule, description, severity, level);
    this.column = column;
    this.message = message;
    this.tool = tool;
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
    CodeViolationData other = (CodeViolationData) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getLine(), other.getLine())
        .append(getColumn(), other.getColumn())
        .append(getRule(), other.getRule())
        .append(getTool(), other.getTool())
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getLine())
        .append(getColumn())
        .append(getRule())
        .append(getTool())
        .toHashCode();
  }

  /**
   * Returns the column number of the violation.
   *
   * @return column
   */
  public final long getColumn() {
    return column;
  }

  /**
   * Returns the message of the violation.
   *
   * @return message
   */
  public final String getMessage() {
    return message;
  }

  /**
   * Returns the name of the tool used.
   *
   * @return tool name
   */
  public final String getTool() {
    return tool;
  }
}
