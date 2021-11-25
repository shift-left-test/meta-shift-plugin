/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * PositiveTreemapData class.
 *
 * @author Sung Gon Kim
 */
public class PositiveTreemapData extends TreemapData {

  private static final long serialVersionUID = -490476241184649272L;

  private final double max;

  /**
   * Default constructor.
   *
   * @param name        of the data
   * @param linesOfCode number of lines
   * @param max         of the data
   * @param value       of the data
   */
  public PositiveTreemapData(String name, long linesOfCode, double max, double value) {
    super(name, linesOfCode, value);
    this.max = max;
  }

  private List<Grade> getGrades() {
    List<Grade> grades = Arrays.asList(Grade.values());
    Collections.reverse(grades);
    return grades;
  }

  @Override
  public int getGrade() {
    List<Grade> grades = getGrades();
    double slot = max / (double) (grades.size() - 1);
    double ratio = getValue();
    for (int i = 0; i < grades.size(); i++) {
      if (slot * i <= ratio && ratio < slot * (i + 1)) {
        return grades.get(i).ordinal();
      }
    }
    return grades.get(grades.size() - 1).ordinal();
  }
}
