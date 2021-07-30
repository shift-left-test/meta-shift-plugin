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
