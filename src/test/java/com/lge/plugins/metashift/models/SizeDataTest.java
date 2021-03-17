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

import com.lge.plugins.metashift.models.SizeSet;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the SizeData class
 *
 * @author Sung Gon Kim
 */
public class SizeDataTest {
  private SizeData origin = new SizeData("A", "a.file", 100, 50, 10);
  private SizeData same = new SizeData("A", "a.file", 3, 2, 1);

  @Test
  public void testInitialization() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals(100, origin.getLines());
    assertEquals(50, origin.getFunctions());
    assertEquals(10, origin.getClasses());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new SizeData("A", "b.file", 100, 50, 10));
    assertNotEquals(origin, new SizeData("B", "a.file", 100, 50, 10));
    assertNotEquals(origin, new SizeData("B", "b.file", 100, 50, 10));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new SizeData("B", "a.file", 100, 50, 10).hashCode());
    assertNotEquals(origin.hashCode(), new SizeData("A", "b.file", 100, 50, 10).hashCode());
    assertEquals(origin.hashCode(), new SizeData("A", "a.file", 0, 50, 10).hashCode());
    assertEquals(origin.hashCode(), new SizeData("A", "a.file", 100, 0, 10).hashCode());
    assertEquals(origin.hashCode(), new SizeData("A", "a.file", 100, 50, 0).hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<SizeData> expected = new ArrayList<>();
    expected.add(new SizeData("A", "a.file", 3, 2, 1));
    expected.add(new SizeData("A", "b.file", 3, 2, 1));
    expected.add(new SizeData("B", "a.file", 3, 2, 1));

    List<SizeData> actual = new ArrayList<>();
    actual.addAll(expected);
    Collections.shuffle(actual);
    Collections.sort(actual);

    assertEquals(expected, actual);
  }
}
