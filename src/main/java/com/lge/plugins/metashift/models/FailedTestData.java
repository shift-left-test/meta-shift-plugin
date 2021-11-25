/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the failed test data.
 *
 * @author Sung Gon Kim
 */
public final class FailedTestData extends TestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 195117557746969885L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param name    of the test
   * @param message of the test
   */
  public FailedTestData(final String recipe, final String suite, final String name,
      final String message) {
    super(recipe, suite, name, message, Status.FAILED);
  }
}
