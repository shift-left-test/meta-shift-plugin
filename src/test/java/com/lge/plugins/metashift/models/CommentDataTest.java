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
 * Unit tests for the CommentData class.
 *
 * @author Sung Gon Kim
 */
public class CommentDataTest {
  private CommentData origin = new CommentData("A", "a.file", 10, 5);
  private CommentData same = new CommentData("A", "a.file", 10, 5);

  @Test
  public void testInitData() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals(10, origin.getLines());
    assertEquals(5, origin.getCommentLines());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new CommentData("B", "a.file", 10, 5));
    assertNotEquals(origin, new CommentData("A", "b.file", 10, 5));
    assertEquals(origin, new CommentData("A", "a.file", 10000, 5));
    assertEquals(origin, new CommentData("A", "a.file", 10, 1000));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new CommentData("B", "a.file", 10, 5).hashCode());
    assertNotEquals(origin.hashCode(), new CommentData("A", "b.file", 10, 5).hashCode());
    assertEquals(origin.hashCode(), new CommentData("A", "a.file", 10000, 5).hashCode());
    assertEquals(origin.hashCode(), new CommentData("A", "a.file", 10, 1000).hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<CommentData> expected = new ArrayList<>();
    expected.add(new CommentData("A", "a.file", 10, 5));
    expected.add(new CommentData("A", "b.file", 10, 5));
    expected.add(new CommentData("B", "a.file", 10, 5));
    expected.add(new CommentData("B", "b.file", 10, 5));

    List<CommentData> actual = new ArrayList<>();
    actual.add(new CommentData("B", "b.file", 10, 5));
    actual.add(new CommentData("B", "a.file", 10, 5));
    actual.add(new CommentData("A", "b.file", 10, 5));
    actual.add(new CommentData("A", "a.file", 10, 5));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
