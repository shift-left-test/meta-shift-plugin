/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the LruCache class.
 *
 * @author Sung Gon Kim
 */
public class LruCacheTest {

  private LruCache<String, String> cache;

  @Before
  public void setUp() {
    cache = new LruCache<>(2);
  }

  @Test
  public void testInitialState() {
    assertEquals(0, cache.size());
  }

  @Test
  public void testAddData() {
    cache.put("A", "1");
    cache.put("B", "2");
    assertEquals(2, cache.size());
  }

  @Test
  public void testAddExceedingNumberOfData() {
    cache.put("A", "1");
    cache.put("B", "2");
    cache.put("C", "3");
    assertEquals(2, cache.size());
    assertTrue(cache.containsKey("B"));
    assertTrue(cache.containsKey("C"));
  }
}
