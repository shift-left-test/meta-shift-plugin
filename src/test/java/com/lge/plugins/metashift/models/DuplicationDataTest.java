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

import java.util.HashSet;
import org.junit.Test;

/**
 * Unit tests for the DuplicationData class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationDataTest {

  private final DuplicationData origin = new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 10);
  private final DuplicationData same = new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 10);

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
    assertEquals(50, origin.getLines());
    assertEquals(1, origin.getStart());
    assertEquals(10, origin.getEnd());
    assertEquals(9, origin.getDuplicatedLines());
    assertEquals(new HashSet<>(), origin.getDuplicateBlocks());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 50, 1, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 50, 1, 10));
    assertEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 500, 1, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 10, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 100));
  }

  @Test
  public void testHashCode() {
    assertHashNotEquals(origin, new Object());
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 50, 1, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 50, 1, 10));
    assertHashEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 500, 1, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 10, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 100));
  }
}
