/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the generic violation data class.
 *
 * @author Sung Gon Kim
 */
public abstract class ViolationData extends Data {

  /**
   * Represents the violation level.
   */
  protected enum Level {
    MAJOR,
    MINOR,
    INFO,
  }

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -5756966538790449784L;

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
  private final Level level;

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
      final String description, final String severity, final Level level) {
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
    return level.name();
  }
}
