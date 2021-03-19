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

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheData class.
 *
 * @author Sung Gon Kim
 */
public class CacheDataTest {
  private CacheData origin = new PremirrorCacheData("A", "do_run", true);
  private CacheData same = new PremirrorCacheData("A", "do_run", true);

  @Test
  public void testInitialization() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("do_run", origin.getTask());
    assertTrue(origin.isAvailable());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new PremirrorCacheData("B", "do_run", true));
    assertNotEquals(origin, new PremirrorCacheData("A", "do_fetch", true));
    assertNotEquals(origin, new PremirrorCacheData("A", "do_run", false));
    assertNotEquals(origin, new SharedStateCacheData("A", "do_run", true));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new PremirrorCacheData("B", "do_run", true).hashCode());
    assertNotEquals(origin.hashCode(), new PremirrorCacheData("A", "do_run2", true).hashCode());
    assertEquals(origin.hashCode(), new PremirrorCacheData("A", "do_run", false).hashCode());
    assertNotEquals(origin.hashCode(), new SharedStateCacheData("A", "do_run", true).hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<CacheData> expected = new ArrayList<>();
    expected.add(new SharedStateCacheData("A", "do_compile", true));
    expected.add(new SharedStateCacheData("A", "do_fetch", true));
    expected.add(new SharedStateCacheData("B", "do_compile", true));

    List<CacheData> actual = new ArrayList<>();
    actual.add(new SharedStateCacheData("B", "do_compile", true));
    actual.add(new SharedStateCacheData("A", "do_fetch", true));
    actual.add(new SharedStateCacheData("A", "do_compile", true));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
