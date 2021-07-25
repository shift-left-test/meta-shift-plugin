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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TreemapData class.
 *
 * @author Sung Gon Kim
 */
public class TreemapDataTest {

  private TreemapData object;

  @Before
  public void setUp() {
    object = new TreemapData("A-B-C", 123, 0.5);
  }

  private void assertValues(TreemapData o, String recipe, long linesOfCode, double value) {
    assertEquals(recipe, o.getRecipe());
    assertEquals(linesOfCode, o.getLinesOfCode());
    assertEquals(value, o.getValue(), 0.01);
  }

  @Test
  public void testCreateObject() {
    assertValues(object, "A-B-C", 123, 0.5);
  }

  @Test
  public void testCreateObjectWithNegativeValue() {
    object = new TreemapData("X-X-X", 456, -100.0);
    assertValues(object, "X-X-X", 456, 0.0);
  }
}
