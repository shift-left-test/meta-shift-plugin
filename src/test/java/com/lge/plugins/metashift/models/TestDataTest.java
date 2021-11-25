/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the TestData class.
 *
 * @author Sung Gon Kim
 */
public class TestDataTest {

  private final TestData origin = new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg");
  private final TestData same = new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg");

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getName());
    assertEquals("a.suite", origin.getSuite());
    assertEquals("a.tc", origin.getTest());
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
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new FailedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertHashNotEquals(origin, new ErrorTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertHashNotEquals(origin, new SkippedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertHashNotEquals(origin, new PassedTestData("B-1.0.0-r0", "a.suite", "a.tc", "msg"));
    assertHashNotEquals(origin, new PassedTestData("A-1.0.0-r0", "b.suite", "a.tc", "msg"));
    assertHashNotEquals(origin, new PassedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    assertHashEquals(origin, new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "X"));
  }
}
