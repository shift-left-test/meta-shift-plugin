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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Counter class.
 *
 * @author Sung Gon Kim
 */
public class CounterTest {

  private Counter object;

  @Before
  public void setUp() {
    object = new Counter(10, 5);
  }

  private void assertValues(Counter o, long numerator, double ratio) {
    assertEquals(numerator, o.getCount());
    assertEquals(ratio, o.getRatio(), 0.01);
  }

  @Test
  public void testCreateObject() {
    assertValues(object, 5, 0.5);
  }

  @Test
  public void testCopyConstructor() {
    Counter copied = new Counter(object);
    assertValues(copied, 5, 0.5);
  }

  @Test
  public void testCreateObjectWithZeroDenominator() {
    object = new Counter(0, 1);
    assertValues(object, 1, 0.0);
  }
}
