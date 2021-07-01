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
 * Unit tests for the MutationTestData class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestDataTest {

  private final MutationTestData origin =
      new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC");
  private final MutationTestData same =
      new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC");

  @Test
  public void testInitData() {
    assertEquals("A-X-X", origin.getRecipe());
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
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(),
        new SurvivedMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("B-X-X", "a.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "b.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "a.file", "D", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "a.file", "C", "g()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 2, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "BOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(),
        new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC2").hashCode());
  }

  @Test
  public void testComparable() {
    List<MutationTestData> expected = new ArrayList<>();
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "XX"));
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "C", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A-X-X", "a.file", "X", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A-X-X", "x.file", "X", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("X-X-X", "x.file", "X", "x()", 2, "XXX", "XX"));

    List<MutationTestData> actual = new ArrayList<>();
    actual.add(new KilledMutationTestData("X-X-X", "x.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "x.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "C", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "XX"));
    actual.add(new KilledMutationTestData("A-X-X", "a.file", "C", "f()", 1, "AOR", "TC"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-X-X", object.getString("recipe"));
    assertEquals("a.file", object.getString("file"));
    assertEquals("C", object.getString("mutatedClass"));
    assertEquals("f()", object.getString("mutatedMethod"));
    assertEquals(1, object.getLong("line"));
    assertEquals("AOR", object.getString("mutator"));
    assertEquals("TC", object.getString("killingTest"));
    assertEquals("KILLED", object.getString("status"));
  }
}
