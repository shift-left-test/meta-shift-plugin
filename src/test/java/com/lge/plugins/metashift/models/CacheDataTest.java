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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Unit tests for the CacheData class.
 *
 * @author Sung Gon Kim
 */
public class CacheDataTest {

  private final CacheData origin = new PremirrorCacheData("A-1.0.0-r0", "X", true);
  private final CacheData same = new PremirrorCacheData("A-1.0.0-r0", "X", true);

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getRecipe());
    assertEquals("X", origin.getSignature());
    assertTrue(origin.isAvailable());
    assertEquals("Premirror", origin.getType());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X", true));
    assertNotEquals(origin, new PremirrorCacheData("B-1.0.0-r0", "X", true));
    assertNotEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "Y", true));
    assertEquals(origin, new PremirrorCacheData("A-1.0.0-r0", "X", false));
    assertNotEquals(origin, new SharedStateCacheData("A-1.0.0-r0", "X:do_A", true));
  }

  @Test
  public void testHashCode() {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(),
        new SharedStateCacheData("A-1.0.0-r0", "X", true).hashCode());
    assertNotEquals(origin.hashCode(),
        new PremirrorCacheData("B-1.0.0-r0", "X", true).hashCode());
    assertNotEquals(origin.hashCode(),
        new PremirrorCacheData("A-1.0.0-r0", "Y", true).hashCode());
    assertEquals(origin.hashCode(),
        new PremirrorCacheData("A-1.0.0-r0", "X", false).hashCode());
    assertNotEquals(origin.hashCode(),
        new SharedStateCacheData("A-1.0.0-r0", "X:do_A", true).hashCode());
  }

  @Test
  public void testComparable() {
    List<CacheData> expected = new ArrayList<>();
    expected.add(new SharedStateCacheData("A-1.0.0-r0", "X:do_compile", true));
    expected.add(new SharedStateCacheData("A-1.0.0-r0", "X:do_fetch", true));
    expected.add(new SharedStateCacheData("B-1.0.0-r0", "X:do_compile", true));

    List<CacheData> actual = new ArrayList<>();
    actual.add(new SharedStateCacheData("B-1.0.0-r0", "X:do_compile", true));
    actual.add(new SharedStateCacheData("A-1.0.0-r0", "X:do_fetch", true));
    actual.add(new SharedStateCacheData("A-1.0.0-r0", "X:do_compile", true));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
