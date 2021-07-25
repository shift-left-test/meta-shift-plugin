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
 * Unit tests for the Distribution class.
 *
 * @author Sung Gon Kim
 */
public class DistributionTest {

  private Distribution pair;
  private Distribution triplet;
  private Distribution quartet;

  @Before
  public void setUp() {
    pair = new Distribution(3, 2, 1, 0);
    triplet = new Distribution(5, 4, 3, 2, 1, 0);
    quartet = new Distribution(7, 6, 5, 4, 3, 2, 1, 0);
  }

  private void assertValues(Counter o, long numerator, double ratio) {
    assertEquals(numerator, o.getCount());
    assertEquals(ratio, o.getRatio(), 0.01);
  }

  @Test
  public void testCreatePairObject() {
    assertValues(pair.getFirst(), 2, 0.66);
    assertValues(pair.getSecond(), 0, 0.0);
    assertValues(pair.getThird(), 0, 0.0);
    assertValues(pair.getFourth(), 0, 0.0);
    assertEquals(4, pair.getTotal());
  }

  @Test
  public void testCopyPairObject() {
    Distribution copied = new Distribution(pair);
    assertValues(copied.getFirst(), 2, 0.66);
    assertValues(copied.getSecond(), 0, 0.0);
    assertValues(copied.getThird(), 0, 0.0);
    assertValues(copied.getFourth(), 0, 0.0);
    assertEquals(4, copied.getTotal());
  }

  @Test
  public void testCreateTripletObject() {
    assertValues(triplet.getFirst(), 4, 0.8);
    assertValues(triplet.getSecond(), 2, 0.66);
    assertValues(triplet.getThird(), 0, 0.0);
    assertValues(triplet.getFourth(), 0, 0.0);
    assertEquals(9, triplet.getTotal());
  }

  @Test
  public void testCopyTripletObject() {
    Distribution copied = new Distribution(triplet);
    assertValues(copied.getFirst(), 4, 0.8);
    assertValues(copied.getSecond(), 2, 0.66);
    assertValues(copied.getThird(), 0, 0.0);
    assertValues(copied.getFourth(), 0, 0.0);
    assertEquals(9, copied.getTotal());
  }

  @Test
  public void testCreateQuartetObject() {
    quartet = new Distribution(7, 6, 5, 4, 3, 2, 1, 0);
    assertValues(quartet.getFirst(), 6, 0.85);
    assertValues(quartet.getSecond(), 4, 0.8);
    assertValues(quartet.getThird(), 2, 0.66);
    assertValues(quartet.getFourth(), 0, 0.0);
    assertEquals(16, quartet.getTotal());
  }

  @Test
  public void testCopyQuartetObject() {
    Distribution copied = new Distribution(quartet);
    assertValues(copied.getFirst(), 6, 0.85);
    assertValues(copied.getSecond(), 4, 0.8);
    assertValues(copied.getThird(), 2, 0.66);
    assertValues(copied.getFourth(), 0, 0.0);
    assertEquals(16, copied.getTotal());
  }
}
