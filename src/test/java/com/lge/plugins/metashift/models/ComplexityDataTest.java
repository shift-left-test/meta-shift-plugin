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

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the ComplexityData class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataTest {
  private ComplexityData origin = new ComplexityData("A", "a.file", "f()", 1);
  private ComplexityData same = new ComplexityData("A", "a.file", "f()", 1);

  @Test
  public void testInitData() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals("f()", origin.getFunction());
    assertEquals(1, origin.getValue());
  }

  @Test
  public void testEquality() throws Exception {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new ComplexityData("B", "a.file", "f()", 1));
    assertNotEquals(origin, new ComplexityData("A", "b.file", "f()", 1));
    assertNotEquals(origin, new ComplexityData("A", "a.file", "x()", 1));
    assertEquals(origin, new ComplexityData("A", "a.file", "f()", 2));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), origin.hashCode());
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new ComplexityData("B", "a.file", "f()", 1).hashCode());
    assertNotEquals(origin.hashCode(), new ComplexityData("A", "b.file", "f()", 1).hashCode());
    assertNotEquals(origin.hashCode(), new ComplexityData("A", "a.file", "x()", 1).hashCode());
    assertEquals(origin.hashCode(), new ComplexityData("A", "a.file", "f()", 2).hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<ComplexityData> expected = new ArrayList<>();
    expected.add(new ComplexityData("A", "a.file", "f()", 1));
    expected.add(new ComplexityData("A", "a.file", "g()", 1));
    expected.add(new ComplexityData("A", "b.file", "g()", 1));
    expected.add(new ComplexityData("B", "b.file", "g()", 1));

    List<ComplexityData> actual = new ArrayList<>();
    actual.add(new ComplexityData("B", "b.file", "g()", 1));
    actual.add(new ComplexityData("A", "b.file", "g()", 1));
    actual.add(new ComplexityData("A", "a.file", "g()", 1));
    actual.add(new ComplexityData("A", "a.file", "f()", 1));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
