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
 * Represents the generic violation data class.
 *
 * @author Sung Gon Kim
 */
public abstract class ViolationData<T> extends Data<T> {

  /**
   * Represents the file.
   */
  private final String file;

  /**
   * Represents the line number.
   */
  private final long line;

  /**
   * Represents the rule.
   */
  private final String rule;

  /**
   * Represents the description of the violation.
   */
  private final String description;

  /**
   * Represents the severity of the violation.
   */
  private final String severity;

  /**
   * Represents the level of the violation.
   */
  private final String level;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param description of the violation
   * @param severity    of the violation
   * @param level       of the violation
   */
  public ViolationData(final String recipe, final String file, final long line, final String rule,
      final String description, final String severity, final String level) {
    super(recipe);
    this.file = file;
    this.line = line;
    this.rule = rule;
    this.description = description;
    this.severity = severity;
    this.level = level;
  }

  /**
   * Returns the file of the violation.
   *
   * @return file
   */
  public final String getFile() {
    return file;
  }

  /**
   * Returns the line number of the violation.
   *
   * @return line number
   */
  public final long getLine() {
    return line;
  }

  /**
   * Returns the rule of the violation.
   *
   * @return rule
   */
  public final String getRule() {
    return rule;
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
   * Returns the severity of the violation.
   *
   * @return severity
   */
  public final String getSeverity() {
    return severity;
  }

  /**
   * Returns the level of the violation.
   *
   * @return level of the violation
   */
  public final String getLevel() {
    return level;
  }
}
