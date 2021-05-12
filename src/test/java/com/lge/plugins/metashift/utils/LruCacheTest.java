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
