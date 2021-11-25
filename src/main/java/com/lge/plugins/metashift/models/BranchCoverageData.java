/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the branch coverage data.
 *
 * @author Sung Gon Kim
 */
public final class BranchCoverageData extends CoverageData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -8559015880405926852L;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param file    name
   * @param line    number
   * @param index   coverage item index
   * @param covered coverage status
   */
  public BranchCoverageData(String recipe, String file, long line, long index, boolean covered) {
    super(recipe, file, line, index, covered, Type.BRANCH);
  }
}
