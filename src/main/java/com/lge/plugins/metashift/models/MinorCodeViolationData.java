/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the minor code violation data.
 *
 * @author Sung Gon Kim
 */
public final class MinorCodeViolationData extends CodeViolationData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -8848887420888707724L;

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
  public MinorCodeViolationData(final String recipe, final String file,
      final long line, final long column,
      final String rule, final String message,
      final String description,
      final String severity, final String tool) {
    super(recipe, file, line, column, rule,
        message, description, severity, tool, Level.MINOR);
  }
}
