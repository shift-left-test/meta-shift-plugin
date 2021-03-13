/*
 * MIT License
 *
 * Copyright (c) 2021 Sung Gon Kim
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

package com.lge.plugins.metashift;


/**
 * Cache Qualifier class
 */
public class CacheQualifier extends Visitor implements Qualifiable {
  private CacheCollector premirror;
  private CacheCollector sharedState;
  private float threshold;

  public CacheQualifier(float threshold) {
    this.premirror = new CacheCollector(Caches.Type.PREMIRROR);
    this.sharedState = new CacheCollector(Caches.Type.SHAREDSTATE);
    this.threshold = threshold;
  }

  public CacheCollector collection(Caches.Type type) {
    switch (type) {
      case PREMIRROR:
        return premirror;
      case SHAREDSTATE:
        return sharedState;
      default:
        throw new IllegalArgumentException("Unknown Cache Type: " + type);
    }
  }

  @Override
  public boolean isAvailable() {
    return (premirror.getDenominator() + sharedState.getDenominator()) > 0;
  }

  @Override
  public boolean isQualified() {
    int denominator = premirror.getDenominator() + sharedState.getDenominator();
    int numerator = premirror.getNumerator() + sharedState.getNumerator();
    if (denominator == 0) {
      return false;
    }
    return ((float) numerator / (float) denominator) >= threshold;
  }

  @Override
  public void visit(Caches objects) {
    objects.accept(premirror);
    objects.accept(sharedState);
  }
}
