/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the killed mutation test data.
 *
 * @author Sung Gon Kim
 */
public final class KilledMutationTestData extends MutationTestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 7773397151160254632L;

  /**
   * Default constructor.
   *
   * @param recipe        name
   * @param file          name
   * @param mutatedClass  the mutated class
   * @param mutatedMethod the mutated method
   * @param line          the line number
   * @param mutator       the mutation operator
   * @param killingTest   the test that kills the mutant
   */
  public KilledMutationTestData(final String recipe, final String file,
      final String mutatedClass, final String mutatedMethod,
      final long line, final String mutator,
      final String killingTest) {
    super(recipe, file, mutatedClass, mutatedMethod, line, mutator, killingTest, Status.KILLED);
  }
}
