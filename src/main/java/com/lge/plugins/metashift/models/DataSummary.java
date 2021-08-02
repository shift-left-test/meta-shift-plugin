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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DataSummary class.
 *
 * @author Sung Gon Kim
 */
public class DataSummary extends Data {

  private static final long serialVersionUID = 6768623147726489246L;

  private final LinesOfCode linesOfCode;
  private final Distribution distribution;
  private final Evaluation evaluation;

  /**
   * Default constructor.
   *
   * @param name         of the data
   * @param linesOfCode  value
   * @param distribution value
   * @param evaluation   value
   */
  public DataSummary(String name, LinesOfCode linesOfCode, Distribution distribution,
      Evaluation evaluation) {
    super(name);
    this.linesOfCode = linesOfCode;
    this.distribution = distribution;
    this.evaluation = evaluation;
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public LinesOfCode getLinesOfCode() {
    return linesOfCode;
  }

  /**
   * Returns the distribution.
   *
   * @return distribution
   */
  public Distribution getDistribution() {
    return distribution;
  }

  /**
   * Returns the evaluation.
   *
   * @return evaluation
   */
  public Evaluation getEvaluation() {
    return evaluation;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    DataSummary other = (DataSummary) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .toHashCode();
  }
}
