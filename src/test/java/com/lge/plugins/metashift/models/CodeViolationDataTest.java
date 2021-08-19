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
 * Unit tests for the CodeViolationData class.
 *
 * @author Sung Gon Kim
 */
public class CodeViolationDataTest {

  private final CodeViolationData origin = new MajorCodeViolationData("A-X-X", "a.file",
      1, 2, "rule", "msg", "desc", "E", "tool");
  private final CodeViolationData same = new MajorCodeViolationData("A-X-X", "a.file",
      1, 2, "rule", "msg", "desc", "E", "tool");

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-X-X", origin.getName());
    assertEquals("a.file", origin.getFile());
    assertEquals(1, origin.getLine());
    assertEquals(2, origin.getColumn());
    assertEquals("rule", origin.getRule());
    assertEquals("msg", origin.getMessage());
    assertEquals("desc", origin.getDescription());
    assertEquals("E", origin.getSeverity());
    assertEquals("tool", origin.getTool());
    assertEquals("MAJOR", origin.getLevel());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin,
        new MinorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new InfoCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("B-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "b.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 9, 2, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 9, "rule", "msg", "desc", "E", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "X", "msg", "desc", "E", "tool"));
    assertEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "X", "desc", "E", "tool"));
    assertEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "X", "E", "tool"));
    assertEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "X", "tool"));
    assertNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "X"));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin,
        new MinorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new InfoCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("B-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "b.file", 1, 2, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 9, 2, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 9, "rule", "msg", "desc", "E", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "X", "msg", "desc", "E", "tool"));
    assertHashEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "X", "desc", "E", "tool"));
    assertHashEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "X", "E", "tool"));
    assertHashEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "X", "tool"));
    assertHashNotEquals(origin,
        new MajorCodeViolationData("A-X-X", "a.file", 1, 2, "rule", "msg", "desc", "E", "X"));
  }
}
