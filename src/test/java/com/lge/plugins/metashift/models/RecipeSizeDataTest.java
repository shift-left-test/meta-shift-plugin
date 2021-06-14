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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Unit tests for the RecipeSizeData class.
 *
 * @author Sung Gon Kim
 */
public class RecipeSizeDataTest {

  private final RecipeSizeData origin = new RecipeSizeData("A-1.0.0-r0", "a.bb", 10);
  private final RecipeSizeData same = new RecipeSizeData("A-1.0.0-r0", "a.bb", 5);

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getRecipe());
    assertEquals("a.bb", origin.getFile());
    assertEquals(10, origin.getLines());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new RecipeSizeData("A-1.0.0-r0", "b.bb", 10));
    assertNotEquals(origin, new RecipeSizeData("B-1.0.0-r0", "b.bb", 10));
  }

  @Test
  public void testHashCode() {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new RecipeSizeData("A-1.0.0-r0", "b.bb", 10).hashCode());
    assertNotEquals(origin.hashCode(), new RecipeSizeData("B-1.0.0-r0", "b.bb", 10).hashCode());
  }

  @Test
  public void testComparable() {
    List<RecipeSizeData> expected = new ArrayList<>();
    expected.add(new RecipeSizeData("A-1.0.0-r0", "a.bb", 10));
    expected.add(new RecipeSizeData("A-1.0.0-r0", "b.bb", 10));
    expected.add(new RecipeSizeData("B-1.0.0-r0", "a.bb", 10));

    List<RecipeSizeData> actual = new ArrayList<>();
    actual.add(new RecipeSizeData("B-1.0.0-r0", "a.bb", 10));
    actual.add(new RecipeSizeData("A-1.0.0-r0", "b.bb", 10));
    actual.add(new RecipeSizeData("A-1.0.0-r0", "a.bb", 10));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
