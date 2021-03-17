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

import com.lge.plugins.metashift.models.Caches;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Caches class
 *
 * @author Sung Gon Kim
 */
public class CachesTest {
  private Caches objects;

  @Before
  public void setUp() throws Exception {
    objects = new Caches();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, objects.size());
  }

  @Test
  public void testAddingData() throws Exception {
    CacheData first = new CacheData("A", "do_compile", true, CacheData.Type.SHAREDSTATE);
    CacheData second = new CacheData("A", "do_fetch", false, CacheData.Type.PREMIRROR);
    objects.add(second);
    objects.add(first);
    assertEquals(2, objects.size());
    assertEquals(first, objects.iterator().next());
  }

  @Test
  public void testAddingDuplicates() throws Exception {
    objects.add(new CacheData("A", "do_fetch", true, CacheData.Type.SHAREDSTATE));
    objects.add(new CacheData("A", "do_fetch", true, CacheData.Type.SHAREDSTATE));
    assertEquals(1, objects.size());
  }
}
