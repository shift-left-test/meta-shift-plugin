/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.Arrays;
import java.util.List;

/**
 * NegativeTreemapData class.
 *
 * @author Sung Gon Kim
 */
public class NegativeTreemapData extends TreemapData {

  private static final long serialVersionUID = -3614276430684611714L;

  private final double min;
  private final double max;

  /**
   * Default constructor.
   *
   * @param name        of the data
   * @param linesOfCode the number of lines
   * @param min         of the data
   * @param max         of the data
   * @param value       of the data
   */
  public NegativeTreemapData(String name, long linesOfCode, double min, double max, double value) {
    super(name, linesOfCode, value);
    this.min = min;
    this.max = max;
  }

  private List<Grade> getGrades() {
    return Arrays.asList(Grade.values());
  }

  @Override
  public int getGrade() {
    List<Grade> grades = getGrades();
    double slot = (max - min) / (double) (grades.size() - 1);
    double ratio = getValue();
    if (ratio <= min) {
      return grades.get(0).ordinal();
    }
    for (int i = 1; i < grades.size(); i++) {
      if (min + (slot * (i - 1)) < ratio && ratio <= min + (slot * i)) {
        return grades.get(i).ordinal();
      }
    }
    return grades.get(grades.size() - 1).ordinal();
  }
}
