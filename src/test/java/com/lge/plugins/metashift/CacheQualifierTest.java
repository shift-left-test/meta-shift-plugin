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
import com.lge.plugins.metashift.CacheQualifier;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class CacheQualifierTest {
  private Caches objects;
  private CacheQualifier qualifier;

  @Before
  public void setUp() throws Exception {
    objects = new Caches();
    qualifier = new CacheQualifier(0.5f);
  }

  @Test
  public void testInitialState() throws Exception {
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.0f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testEmptyCaches() throws Exception {
    objects.accept(qualifier);
    assertFalse(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.0f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testPremirrorOnlyWhichNotQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_B", false, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_C", false, Caches.Type.PREMIRROR));
    objects.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.3f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.1f);
    assertEquals(0.0f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testPremirrorOnlyWhichQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_B", false, Caches.Type.PREMIRROR));
    objects.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
    assertEquals(0.5f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.1f);
    assertEquals(0.0f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.0f);
  }

  @Test
  public void testSharedStateOnlyWhichNotQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_B", false, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_C", false, Caches.Type.SHAREDSTATE));
    objects.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertFalse(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.3f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.1f);
  }

  @Test
  public void testSharedStateOnlyWhichQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_B", false, Caches.Type.SHAREDSTATE));
    objects.accept(qualifier);
    assertTrue(qualifier.isAvailable());
    assertTrue(qualifier.isQualified());
    assertEquals(0.0f, qualifier.collection(Caches.Type.PREMIRROR).getRatio(), 0.0f);
    assertEquals(0.5f, qualifier.collection(Caches.Type.SHAREDSTATE).getRatio(), 0.1f);
  }

  @Test
  public void testMixedCacheWhichNotQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_B", false, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_C", false, Caches.Type.SHAREDSTATE));
    objects.accept(qualifier);
    assertFalse(qualifier.isQualified());
  }

  @Test
  public void testMixedCacheWhichQualified() throws Exception {
    objects.add(new Caches.Data("A", "do_A", true, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_B", true, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_C", false, Caches.Type.SHAREDSTATE));
    objects.accept(qualifier);
    assertTrue(qualifier.isQualified());
  }
}
