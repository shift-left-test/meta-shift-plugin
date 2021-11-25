/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the coverage data class.
 *
 * @author Sung Gon Kim
 */
public class CoverageDataTest {

  private final CoverageData origin =
      new StatementCoverageData("A-B-C", "a.file", 1, true);
  private final CoverageData same =
      new StatementCoverageData("A-B-C", "a.file", 1, false);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-B-C", origin.getName());
    assertEquals("a.file", origin.getFile());
    assertEquals(1, origin.getLine());
    assertEquals(0, origin.getIndex());
    assertTrue(origin.isCovered());
    assertEquals("STATEMENT", origin.getType());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new BranchCoverageData("A-B-C", "a.file", 1, 0, true));
    assertNotEquals(origin, new StatementCoverageData("B-B-C", "a.file", 1, true));
    assertNotEquals(origin, new StatementCoverageData("A-B-C", "b.file", 1, true));
    assertNotEquals(origin, new StatementCoverageData("A-B-C", "a.file", 2, true));
    assertEquals(origin, new StatementCoverageData("A-B-C", "a.file", 1, false));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new BranchCoverageData("A-B-C", "a.file", 1, 0, true));
    assertHashNotEquals(origin, new StatementCoverageData("B-B-C", "a.file", 1, true));
    assertHashNotEquals(origin, new StatementCoverageData("A-B-C", "b.file", 1, true));
    assertHashNotEquals(origin, new StatementCoverageData("A-B-C", "a.file", 2, true));
    assertHashEquals(origin, new StatementCoverageData("A-B-C", "a.file", 1, false));
  }
}
