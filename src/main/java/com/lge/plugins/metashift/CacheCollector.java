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

public class CacheCollector extends Visitor implements Measurable {
  private Caches.Type type;
  private int denominator;
  private int numerator;

  public CacheCollector(Caches.Type type) {
    this.type = type;
    this.denominator = 0;
    this.numerator = 0;
  }

  @Override
  public int getDenominator() {
    return denominator;
  }

  @Override
  public int getNumerator() {
    return numerator;
  }

  @Override
  public float getRatio() {
    return (denominator > 0) ? (float) numerator / (float) denominator : 0.0f;
  }

  @Override
  public void visit(Caches objects) {
    denominator += objects
        .stream()
        .filter(object -> object.getType() == type)
        .count();
    numerator += objects
        .stream()
        .filter(object -> object.getType() == type && object.isAvailable())
        .count();
    return;
  }
}
