/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests for the CacheData class.
 *
 * @author Sung Gon Kim
 */
public class CacheDataTest {

  private final CacheData origin = new PremirrorCacheData("A-1.0.0-r0", "X", true);
  private final CacheData same = new PremirrorCacheData("C-1.0.0-r0", "X", false);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getName());
    assertEquals("X", origin.getSignature());
    assertTrue(origin.isAvailable());
    assertEquals("PREMIRROR", origin.getType());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X", true));
    assertEquals(origin, new PremirrorCacheData("B-1.0.0-r0", "X", true));
    assertNotEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "Y", true));
    assertEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "X", false));
    assertNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X:do_A", true));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X", true));
    assertHashEquals(origin, new PremirrorCacheData("B-1.0.0-r0", "X", true));
    assertHashNotEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "Y", true));
    assertHashEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "X", false));
    assertHashNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X:do_A", true));
  }

  @Test
  public void testUniqueness() {
    Set<CacheData> objects = new HashSet<>();
    objects.add(origin);
    objects.add(same);
    assertEquals(1, objects.size());
  }
}
