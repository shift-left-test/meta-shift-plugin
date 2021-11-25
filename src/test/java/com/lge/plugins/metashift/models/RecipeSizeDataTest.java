/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the RecipeSizeData class.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeDataTest {

  private final RecipeSizeData origin = new RecipeSizeData("A-1.0.0-r0", "a.bb", 10);
  private final RecipeSizeData same = new RecipeSizeData("A-1.0.0-r0", "a.bb", 5);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getName());
    assertEquals("a.bb", origin.getFile());
    assertEquals(10, origin.getLines());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new RecipeSizeData("A-1.0.0-r0", "b.bb", 10));
    assertNotEquals(origin, new RecipeSizeData("B-1.0.0-r0", "b.bb", 10));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new RecipeSizeData("A-1.0.0-r0", "b.bb", 10));
    assertHashNotEquals(origin, new RecipeSizeData("B-1.0.0-r0", "b.bb", 10));
  }
}
