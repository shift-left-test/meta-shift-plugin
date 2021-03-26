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

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.CodeViolationList;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates the level of code violations.
 *
 * @author Sung Gon Kim
 */
public final class CodeViolationQualifier extends Visitor implements Qualifier {
  /**
   * Represents the collectino of CodeViolationCounter objects.
   */
  private Map<Class<? extends CodeViolationData>, CodeViolationCounter> collection;
  /**
   * Represents the threshold of the qualificiation.
   */
  private float threshold;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public CodeViolationQualifier(final float threshold) {
    collection = new HashMap<>();
    collection.put(MajorCodeViolationData.class,
                   new CodeViolationCounter(MajorCodeViolationData.class));
    collection.put(MinorCodeViolationData.class,
                   new CodeViolationCounter(MinorCodeViolationData.class));
    collection.put(InfoCodeViolationData.class,
                   new CodeViolationCounter(InfoCodeViolationData.class));
    this.threshold = threshold;
  }

  /**
   * Returns the relevant collector object based on the given type.
   *
   * @param clazz of the object type to return
   * @return CodeViolationCounter object
   */
  public CodeViolationCounter collection(final Class<? extends CodeViolationData> clazz) {
    return collection.get(clazz);
  }

  @Override
  public boolean isAvailable() {
    return collection(MajorCodeViolationData.class).getDenominator() > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = collection(MajorCodeViolationData.class).getDenominator();
    int numerator = collection(MajorCodeViolationData.class).getNumerator();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) <= threshold;
  }

  @Override
  public void visit(final CodeViolationList objects) {
    collection.values().stream().forEach(collector -> objects.accept(collector));
  }
}
