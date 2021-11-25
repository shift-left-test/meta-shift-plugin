/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the skipped mutation test data.
 *
 * @author Sung Gon Kim
 */
public final class SkippedMutationTestData extends MutationTestData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 6172236786183701568L;

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
  public SkippedMutationTestData(String recipe, String file,
      String mutatedClass, String mutatedMethod,
      long line, String mutator,
      String killingTest) {
    super(recipe, file, mutatedClass, mutatedMethod, line, mutator, killingTest, Status.SKIPPED);
  }
}
