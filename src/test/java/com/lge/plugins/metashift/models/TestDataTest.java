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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the TestData class.
 *
 * @author Sung Gon Kim
 */
public class TestDataTest {

  private final TestData origin = new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg");
  private final TestData same = new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg");

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getRecipe());
    assertEquals("a.suite", origin.getSuite());
    assertEquals("a.tc", origin.getName());
    assertEquals("msg", origin.getMessage());
    assertEquals("PASSED", origin.getStatus());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new FailedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new ErrorTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new SkippedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("B-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("A-1.0.0-r0", "b.suite", "a.tc", "msg"));
    assertNotEquals(origin, new PassedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    assertEquals(origin, new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "X"));
  }

  @Test
  public void testHashCode() {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(),
        new FailedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(),
        new ErrorTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(),
        new SkippedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(),
        new PassedTestData("B-1.0.0-r0", "a.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(),
        new PassedTestData("A-1.0.0-r0", "b.suite", "a.tc", "msg").hashCode());
    assertNotEquals(origin.hashCode(),
        new PassedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg").hashCode());
    assertEquals(origin.hashCode(),
        new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "X").hashCode());
  }

  @Test
  public void testComparable() {
    List<TestData> expected = new ArrayList<>();
    expected.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    expected.add(new PassedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    expected.add(new PassedTestData("A-1.0.0-r0", "b.suite", "b.tc", "msg"));
    expected.add(new PassedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));

    List<TestData> actual = new ArrayList<>();
    actual.add(new PassedTestData("B-1.0.0-r0", "b.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A-1.0.0-r0", "b.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    actual.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-1.0.0-r0", object.getString("recipe"));
    assertEquals("a.suite", object.getString("suite"));
    assertEquals("a.tc", object.getString("name"));
    assertEquals("msg", object.getString("message"));
    assertEquals("PASSED", object.getString("status"));
  }
}
