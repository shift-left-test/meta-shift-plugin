/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the statement coverage data.
 *
 * @author Sung Gon Kim
 */
public final class StatementCoverageData extends CoverageData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -3297820378967412417L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param file    name
   * @param line    number
   * @param covered coverage status
   */
  public StatementCoverageData(String recipe, String file, long line, boolean covered) {
    super(recipe, file, line, 0, covered, Type.STATEMENT);
  }
}
