/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the skipped test data.
 *
 * @author Sung Gon Kim
 */
public final class SkippedTestData extends TestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7790781177757853883L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param name    of the test
   * @param message of the test
   */
  public SkippedTestData(final String recipe, final String suite, final String name,
      final String message) {
    super(recipe, suite, name, message, Status.SKIPPED);
  }

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param name    of the test
   * @param message of the test
   * @param file    source file of the test, empty when unknown
   * @param line    line number of the test, 0 when unknown
   */
  public SkippedTestData(final String recipe, final String suite, final String name,
      final String message, final String file, final long line) {
    super(recipe, suite, name, message, file, line, Status.SKIPPED);
  }
}
