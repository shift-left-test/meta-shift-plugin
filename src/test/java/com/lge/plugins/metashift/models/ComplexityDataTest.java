/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the ComplexityData class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataTest {

  private final ComplexityData origin =
      new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 1);
  private final ComplexityData same =
      new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 1);

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
    assertEquals("f()", origin.getFunction());
    assertEquals(1, origin.getValue());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    assertNotEquals(origin, new ComplexityData("A-1.0.0-r0", "b.file", "f()", 5, 10, 1));
    assertNotEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "x()", 5, 10, 1));
    assertEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 2));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 1));
    assertHashNotEquals(origin, new ComplexityData("A-1.0.0-r0", "b.file", "f()", 5, 10, 1));
    assertHashNotEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "x()", 5, 10, 1));
    assertHashEquals(origin, new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 2));
  }
}
