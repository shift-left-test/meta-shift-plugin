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

/**
 * Evaluates the level of complexities.
 *
 * @author Sung Gon Kim
 */
public final class ComplexityQualifier extends Visitor implements Qualifiable {
  /**
   * Represents the collection of ComplexityCollector objects.
   */
  private ComplexityCollector collection;
  /**
   * Represents the threshold of the qualification.
   */
  private float threshold;

  /**
   * Default constructor.
   *
   * @param complexity threshold for the complexity
   * @param threshold for evaluation
   */
  public ComplexityQualifier(final int complexity, final float threshold) {
    this.collection = new ComplexityCollector(complexity);
    this.threshold = threshold;
  }

  /**
   * Returns the collector object.
   *
   * @return ComplexityCollector object
   */
  public ComplexityCollector collection() {
    return collection;
  }

  @Override
  public boolean isAvailable() {
    return collection.getDenominator() > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = collection().getDenominator();
    int numerator = collection().getNumerator();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) <= threshold;
  }

  @Override
  public void visit(final ComplexitySet objects) {
    objects.accept(collection);
  }
}
