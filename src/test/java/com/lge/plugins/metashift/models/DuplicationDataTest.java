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

import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the DuplicationData class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationDataTest {

  private final DuplicationData origin = new DuplicationData("A-1.0.0-r0", "a.file", 10, 5);
  private final DuplicationData same = new DuplicationData("A-1.0.0-r0", "a.file", 10, 5);

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
    assertEquals(10, origin.getLines());
    assertEquals(5, origin.getDuplicatedLines());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 10, 5));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 10, 5));
    assertEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 10000, 5));
    assertEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 10, 5000));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 10, 5));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 10, 5));
    assertHashEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 10000, 5));
    assertHashEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 10, 5000));
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-1.0.0-r0", object.getString("name"));
    assertEquals("a.file", object.getString("file"));
    assertEquals(10, object.getLong("lines"));
    assertEquals(5, object.getLong("duplicatedLines"));
  }
}
