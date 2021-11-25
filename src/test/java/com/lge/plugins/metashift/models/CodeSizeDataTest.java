/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    assertEquals("A-1.0.0-r0", origin.getName());
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
}
