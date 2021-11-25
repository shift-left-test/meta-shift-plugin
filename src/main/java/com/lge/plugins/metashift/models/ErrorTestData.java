/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the error test data.
 *
 * @author Sung Gon Kim
 */
public final class ErrorTestData extends TestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 5096328120590914694L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param name    of the test
   * @param message of the test
   */
  public ErrorTestData(final String recipe, final String suite, final String name,
      final String message) {
    super(recipe, suite, name, message, Status.ERROR);
  }
}
