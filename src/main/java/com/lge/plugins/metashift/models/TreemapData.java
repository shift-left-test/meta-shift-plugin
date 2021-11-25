/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.io.Serializable;

/**
 * TreemapData class.
 *
 * @author Sung Gon Kim
 */
public abstract class TreemapData implements Serializable {

  /**
   * Grade enumeration.
   */
  public enum Grade {
    BEST,
    BETTER,
    GOOD,
    ORDINARY,
    BAD,
    WORSE,
    WORST,
  }

  private static final long serialVersionUID = -5314580050953091011L;

  private final String name;
  private final long linesOfCode;
  private final double value;

  /**
   * Default constructor.
   *
   * @param name        of the data
   * @param linesOfCode the number of lines
   */
  public TreemapData(String name, long linesOfCode, double value) {
    this.name = name;
    this.linesOfCode = linesOfCode;
    this.value = Math.max(0.0, value);
  }

  /**
   * Returns the name of the data.
   *
   * @return recipe name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public long getLinesOfCode() {
    return linesOfCode;
  }

  /**
   * Returns the value of the data.
   *
   * @return the value of the data
   */
  public double getValue() {
    return value;
  }

  /**
   * Returns the grade of the data.
   *
   * @return grade
   */
  public abstract int getGrade();
}
