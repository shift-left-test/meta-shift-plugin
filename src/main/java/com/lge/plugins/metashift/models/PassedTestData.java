/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the passed test data.
 *
 * @author Sung Gon Kim
 */
public final class PassedTestData extends TestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -733772536699607530L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param name    of the test
   * @param message of the test
   */
  public PassedTestData(final String recipe, final String suite, final String name,
      final String message) {
    super(recipe, suite, name, message, Status.PASSED);
  }
}
