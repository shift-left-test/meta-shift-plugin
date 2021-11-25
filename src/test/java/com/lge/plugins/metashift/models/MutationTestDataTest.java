/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
}
