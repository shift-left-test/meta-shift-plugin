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

import com.lge.plugins.metashift.models.CacheCollector;
import com.lge.plugins.metashift.models.Caches;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the CacheCollector class
 *
 * @author Sung Gon Kim
 */
public class CacheCollectorTest {
  private Caches objects;
  private CacheCollector collector;

  @Before
  public void setUp() throws Exception {
    objects = new Caches();
    collector = new CacheCollector(Caches.Type.SHAREDSTATE);
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testWithEmptyCache() throws Exception {
    objects.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testWithNonMatchingTypeCache() throws Exception {
    objects.add(new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    objects.accept(collector);
    assertEquals(0, collector.getDenominator());
    assertEquals(0, collector.getNumerator());
    assertEquals(0.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testWithMatchingTypeCache() throws Exception {
    objects.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    objects.accept(collector);
    assertEquals(1, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(1.0f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testAvailableCaches() throws Exception {
    objects.add(new Caches.Data("A", "do_fetch", true, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_compile", false, Caches.Type.SHAREDSTATE));
    objects.add(new Caches.Data("A", "do_fetch", true, Caches.Type.PREMIRROR));
    objects.add(new Caches.Data("A", "do_compile", false, Caches.Type.PREMIRROR));
    objects.accept(collector);
    assertEquals(2, collector.getDenominator());
    assertEquals(1, collector.getNumerator());
    assertEquals(0.5f, collector.getRatio(), 0.0f);
  }

  @Test
  public void testMultipleCaches() throws Exception {
    List<Caches> group = new ArrayList<>();
    Caches caches = new Caches();
    caches.add(new Caches.Data("A", "do_test", true, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("A", "do_fetch", false, Caches.Type.SHAREDSTATE));
    group.add(caches);
    caches = new Caches();
    caches.add(new Caches.Data("B", "do_test", true, Caches.Type.SHAREDSTATE));
    caches.add(new Caches.Data("B", "do_fetch", false, Caches.Type.SHAREDSTATE));
    group.add(caches);

    group.forEach(o -> o.accept(collector));
    assertEquals(4, collector.getDenominator());
    assertEquals(2, collector.getNumerator());
    assertEquals(0.5f, collector.getRatio(), 0.0f);
  }
}
