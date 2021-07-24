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
 * Unit tests for the CodeSizeData class.
 *
 * @author Sung Gon Kim
 */
public class CodeSizeDataTest {

  private final CodeSizeData origin = new CodeSizeData("A-1.0.0-r0", "a.file", 100, 50, 10);
  private final CodeSizeData same = new CodeSizeData("A-1.0.0-r0", "a.file", 3, 2, 1);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals(100, origin.getLines());
    assertEquals(50, origin.getFunctions());
    assertEquals(10, origin.getClasses());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new CodeSizeData("A-1.0.0-r0", "b.file", 100, 50, 10));
    assertNotEquals(origin, new CodeSizeData("B-1.0.0-r0", "a.file", 100, 50, 10));
    assertNotEquals(origin, new CodeSizeData("B-1.0.0-r0", "b.file", 100, 50, 10));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new CodeSizeData("B-1.0.0-r0", "a.file", 100, 50, 10));
    assertHashNotEquals(origin, new CodeSizeData("A-1.0.0-r0", "b.file", 100, 50, 10));
    assertHashEquals(origin, new CodeSizeData("A-1.0.0-r0", "a.file", 0, 50, 10));
    assertHashEquals(origin, new CodeSizeData("A-1.0.0-r0", "a.file", 100, 0, 10));
    assertHashEquals(origin, new CodeSizeData("A-1.0.0-r0", "a.file", 100, 50, 0));
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-1.0.0-r0", object.getString("recipe"));
    assertEquals("a.file", object.getString("file"));
    assertEquals(100, object.getLong("lines"));
    assertEquals(50, object.getLong("functions"));
    assertEquals(10, object.getLong("classes"));
  }
}
