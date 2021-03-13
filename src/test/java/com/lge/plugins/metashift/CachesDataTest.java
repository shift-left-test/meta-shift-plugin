/*
 * MIT License
 *
 * Copyright (c) 2021 Sung Gon Kim
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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.Caches;
import org.junit.*;
import static org.junit.Assert.*;

public class CachesDataTest {
  private Caches.Data origin = new Caches.Data("A", "do_run", true, Caches.Type.PREMIRROR);
  private Caches.Data same = new Caches.Data("A", "do_run", true, Caches.Type.PREMIRROR);

  @Test
  public void testInitialization() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("do_run", origin.getTask());
    assertTrue(origin.isAvailable());
    assertEquals(Caches.Type.PREMIRROR, origin.getType());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new Caches.Data("B", "do_run", true, Caches.Type.PREMIRROR));
    assertNotEquals(origin, new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    assertNotEquals(origin, new Caches.Data("A", "do_run", false, Caches.Type.PREMIRROR));
    assertNotEquals(origin, new Caches.Data("A", "do_run", true, Caches.Type.SHAREDSTATE));
  }

  @Test
  public void testHashCode() throws Exception {
    Caches.Data different = new Caches.Data("B", "do_fetch", false, Caches.Type.SHAREDSTATE);
    assertEquals(origin.hashCode(), origin.hashCode());
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), different.hashCode());
  }
}
