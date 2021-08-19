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
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the ComplexityData class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataTest {

  private final ComplexityData origin =
      new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 1);
  private final ComplexityData same =
      new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 1);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getName());
    assertEquals("a.file", origin.getFile());
    assertEquals("f()", origin.getFunction());
    assertEquals(1, origin.getValue());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    assertNotEquals(origin, new ComplexityData("A-1.0.0-r0", "b.file", "f()", 5, 10, 1));
    assertNotEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "x()", 5, 10, 1));
    assertEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 2));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    assertHashNotEquals(origin, new ComplexityData("A-1.0.0-r0", "b.file", "f()", 5, 10, 1));
    assertHashNotEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "x()", 5, 10, 1));
    assertHashEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 2));
  }
}
