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

import java.io.Serializable;
import java.util.List;

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
  private final double min;
  private final double max;
  private final double value;

  /**
   * Default constructor.
   *
   * @param name        of the data
   * @param linesOfCode the number of lines
   * @param min         of the data
   * @param max         of the data
   * @param value       of the data
   */
  public TreemapData(String name, long linesOfCode, double min, double max, double value) {
    this.name = name;
    this.linesOfCode = linesOfCode;
    this.min = min;
    this.max = max;
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

  protected abstract List<Grade> getGrades();

  /**
   * Returns the grade of the data.
   *
   * @return grade
   */
  public int getGrade() {
    List<Grade> grades = getGrades();
    double slot = (max - min) / (double) (grades.size() - 1);
    double ratio = getValue();
    if (ratio == 0.0 || ratio < min) {
      return grades.get(0).ordinal();
    }
    for (int i = 1; i < grades.size() - 1; i++) {
      if (min + (slot * (i - 1)) <= ratio && ratio < min + (slot * i)) {
        return grades.get(i).ordinal();
      }
    }
    return grades.get(grades.size() - 1).ordinal();
  }
}
