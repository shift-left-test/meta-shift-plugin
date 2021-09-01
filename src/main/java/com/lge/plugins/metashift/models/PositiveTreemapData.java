/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
