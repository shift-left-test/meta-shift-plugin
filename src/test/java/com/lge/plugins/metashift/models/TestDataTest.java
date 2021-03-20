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
 * Unit tests for the TestData class.
 *
 * @author Sung Gon Kim
 */
public class TestDataTest {
  private TestData origin = new PassedTestData("A", "a.suite", "a.tc", "msg");
  private TestData same = new PassedTestData("A", "a.suite", "a.tc", "msg");

  @Test
  public void testInitData() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.suite", origin.getSuite());
    assertEquals("a.tc", origin.getName());
    assertEquals("msg", origin.getMessage());
  }

  @Test
  public void testEquality() throws Exception {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new FailedTestData("A", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new ErrorTestData("A", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new SkippedTestData("A", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("B", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("A", "b.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("A", "a.suite", "b.tc", "msg"));
    assertEquals(origin, new PassedTestData("A", "a.suite", "a.tc", "X"));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new FailedTestData("A", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(), new ErrorTestData("A", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(), new SkippedTestData("A", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(), new PassedTestData("B", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(), new PassedTestData("A", "b.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(), new PassedTestData("A", "a.suite", "b.tc", "msg").hashCode());
    assertEquals(origin.hashCode(), new PassedTestData("A", "a.suite", "a.tc", "X").hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<TestData> expected = new ArrayList<>();
    expected.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));
    expected.add(new PassedTestData("A", "a.suite", "b.tc", "msg"));
    expected.add(new PassedTestData("A", "b.suite", "b.tc", "msg"));
    expected.add(new PassedTestData("B", "b.suite", "b.tc", "msg"));

    List<TestData> actual = new ArrayList<>();
    actual.add(new PassedTestData("B", "b.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A", "b.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A", "a.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A", "a.suite", "a.tc", "msg"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
