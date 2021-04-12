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

/**
 * Represents the code violation data.
 *
 * @author Sung Gon Kim
 */
public abstract class CodeViolationData extends Data<CodeViolationData> {

  /**
   * Represents the name fo the file.
   */
  private final String file;

  /**
   * Represents the line number.
   */
  private final long line;

  /**
   * Represents the column number.
   */
  private final long column;

  /**
   * Represents the rule.
   */
  private final String rule;

  /**
   * Represents the message.
   */
  private final String message;

  /**
   * Represents the description.
   */
  private final String description;

  /**
   * Represents the severity.
   */
  private final String severity;

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
   */
  public CodeViolationData(final String recipe, final String file,
      final long line, final long column,
      final String rule, final String message,
      final String description,
      final String severity, final String tool) {
    super(recipe);
    this.file = file;
    this.line = line;
    this.column = column;
    this.rule = rule;
    this.message = message;
    this.description = description;
    this.severity = severity;
    this.tool = tool;
  }

  @Override
  public final int compareTo(final CodeViolationData other) {
    return compareEach(
        getRecipe().compareTo(other.getRecipe()),
        file.compareTo(other.file),
        Long.compare(line, other.line),
        Long.compare(column, other.column),
        rule.compareTo(other.rule),
        tool.compareTo(other.tool)
    );
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
    return compareTo((CodeViolationData) object) == 0;
  }

  @Override
  public final int hashCode() {
    return computeHashCode(getClass(), getRecipe(), file, line, column, rule, tool);
  }

  /**
   * Returns the filename of the violation.
   *
   * @return file
   */
  public final String getFile() {
    return file;
  }

  /**
   * Returns the line number of the violation.
   *
   * @return line
   */
  public final long getLine() {
    return line;
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
   * Returns the rule name of the violation.
   *
   * @return rule
   */
  public final String getRule() {
    return rule;
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
   * Returns the description of the violation.
   *
   * @return description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Returns the severity of the rule.
   *
   * @return severity
   */
  public final String getSeverity() {
    return severity;
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
