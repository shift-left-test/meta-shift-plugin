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

import com.lge.plugins.metashift.models.CacheSet;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheData class
 *
 * @author Sung Gon Kim
 */
public class CacheDataTest {
  private CacheData origin = new CacheData("A", "do_run", true, CacheData.Type.PREMIRROR);
  private CacheData same = new CacheData("A", "do_run", true, CacheData.Type.PREMIRROR);

  @Test
  public void testInitialization() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("do_run", origin.getTask());
    assertTrue(origin.isAvailable());
    assertEquals(CacheData.Type.PREMIRROR, origin.getType());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new CacheData("B", "do_run", true, CacheData.Type.PREMIRROR));
    assertNotEquals(origin, new CacheData("A", "do_fetch", true, CacheData.Type.PREMIRROR));
    assertNotEquals(origin, new CacheData("A", "do_run", false, CacheData.Type.PREMIRROR));
    assertNotEquals(origin, new CacheData("A", "do_run", true, CacheData.Type.SHAREDSTATE));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new CacheData("B", "do_run", true, CacheData.Type.PREMIRROR).hashCode());
    assertNotEquals(origin.hashCode(), new CacheData("A", "do_other", true, CacheData.Type.PREMIRROR).hashCode());
    assertEquals(origin.hashCode(), new CacheData("A", "do_run", false, CacheData.Type.PREMIRROR).hashCode());
    assertNotEquals(origin.hashCode(), new CacheData("A", "do_run", true, CacheData.Type.SHAREDSTATE).hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<CacheData> expected = new ArrayList<>();
    expected.add(new CacheData("A", "do_compile", true, CacheData.Type.SHAREDSTATE));
    expected.add(new CacheData("A", "do_fetch", true, CacheData.Type.SHAREDSTATE));
    expected.add(new CacheData("B", "do_compile", true, CacheData.Type.SHAREDSTATE));

    List<CacheData> actual = new ArrayList<>();
    actual.addAll(expected);
    Collections.shuffle(actual);
    Collections.sort(actual);

    assertEquals(expected, actual);
  }
}
