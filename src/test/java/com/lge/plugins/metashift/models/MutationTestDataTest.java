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
 * Unit tests for the MutationTestData class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestDataTest {
  private MutationTestData origin =
      new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC");
  private MutationTestData same =
      new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC");

  @Test
  public void testInitData() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals("C", origin.getMutatedClass());
    assertEquals("f()", origin.getMutatedMethod());
    assertEquals(1, origin.getLine());
    assertEquals("AOR", origin.getMutator());
    assertEquals("TC", origin.getKillingTest());
  }

  @Test
  public void testEquality() throws Exception {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("B", "a.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "a.file", "D", "f()", 1, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "a.file", "C", "g()", 1, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "a.file", "C", "f()", 2, "AOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "a.file", "C", "f()", 1, "BOR", "TC"));
    assertNotEquals(origin, new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC2"));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new SurvivedMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("B", "a.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "b.file", "C", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "a.file", "D", "f()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "a.file", "C", "g()", 1, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "a.file", "C", "f()", 2, "AOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "a.file", "C", "f()", 1, "BOR", "TC").hashCode());
    assertNotEquals(origin.hashCode(), new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC2").hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<MutationTestData> expected = new ArrayList<>();
    expected.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));
    expected.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "XX"));
    expected.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A", "a.file", "C", "f()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A", "a.file", "C", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A", "a.file", "X", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("A", "x.file", "X", "x()", 2, "XXX", "XX"));
    expected.add(new KilledMutationTestData("X", "x.file", "X", "x()", 2, "XXX", "XX"));

    List<MutationTestData> actual = new ArrayList<>();
    actual.add(new KilledMutationTestData("X", "x.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "x.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "X", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "C", "x()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "C", "f()", 2, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "XXX", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "XX"));
    actual.add(new KilledMutationTestData("A", "a.file", "C", "f()", 1, "AOR", "TC"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
