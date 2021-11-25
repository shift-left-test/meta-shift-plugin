/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import org.junit.Test;

/**
 * Unit tests for the DuplicationData class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationDataTest {

  private final DuplicationData origin = new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 10);
  private final DuplicationData same = new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 10);

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
    assertEquals(50, origin.getLines());
    assertEquals(1, origin.getStart());
    assertEquals(10, origin.getEnd());
    assertEquals(9, origin.getDuplicatedLines());
    assertEquals(new HashSet<>(), origin.getDuplicateBlocks());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 50, 1, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 50, 1, 10));
    assertEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 500, 1, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 10, 10));
    assertNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 100));
  }

  @Test
  public void testHashCode() {
    assertHashNotEquals(origin, new Object());
    assertHashEquals(origin, origin);
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new DuplicationData("B-1.0.0-r0", "a.file", 50, 1, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "b.file", 50, 1, 10));
    assertHashEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 500, 1, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 10, 10));
    assertHashNotEquals(origin, new DuplicationData("A-1.0.0-r0", "a.file", 50, 1, 100));
  }
}
