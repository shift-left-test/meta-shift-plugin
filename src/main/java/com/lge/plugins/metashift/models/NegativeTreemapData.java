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
