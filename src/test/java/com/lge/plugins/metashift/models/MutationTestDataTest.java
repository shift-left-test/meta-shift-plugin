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
 * Unit tests for the MutationTestData class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestDataTest {

  private final MutationTestData origin =
      new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC");
  private final MutationTestData same =
      new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC");

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
    assertEquals("C", origin.getMutatedClass());
    assertEquals("f()", origin.getMutatedMethod());
    assertEquals(1, origin.getLine());
    assertEquals("AOR", origin.getMutator());
    assertEquals("TC", origin.getKillingTest());
    assertEquals("KILLED", origin.getStatus());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin,
        new SurvivedMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new SkippedMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("B-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "b.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "D", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "g()", 1, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 2, "AOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "BOR", "TC"));
    assertNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC2"));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin,
        new SurvivedMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("B-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "b.file", "C", "f()", 1, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "D", "f()", 1, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "g()", 1, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 2, "AOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "BOR", "TC"));
    assertHashNotEquals(origin,
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC2"));
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-X-X", object.getString("name"));
    assertEquals("a.file", object.getString("file"));
    assertEquals("C", object.getString("mutatedClass"));
    assertEquals("f()", object.getString("mutatedMethod"));
    assertEquals(1, object.getLong("line"));
    assertEquals("AOR", object.getString("mutator"));
    assertEquals("TC", object.getString("killingTest"));
    assertEquals("KILLED", object.getString("status"));
  }
}
