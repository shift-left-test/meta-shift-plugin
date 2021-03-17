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

import com.lge.plugins.metashift.models.Sizes;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Sizes class
 *
 * @author Sung Gon Kim
 */
public class SizesTest {
  private Sizes sizes;

  @Before
  public void setUp() throws Exception {
    sizes = new Sizes();
  }

  @Test
  public void testInitialState() throws Exception {
    assertEquals(0, sizes.size());
  }

  @Test
  public void testAddingData() throws Exception {
    SizeData first = new SizeData("A", "a.file", 3, 2, 1);
    SizeData second = new SizeData("B", "b.file", 3, 2, 1);
    sizes.add(second);
    sizes.add(first);
    assertEquals(2, sizes.size());
    assertEquals(first, sizes.iterator().next());
  }

  @Test
  public void testAddingDuplicates() throws Exception {
    sizes.add(new SizeData("A", "a.file", 3, 2, 1));
    sizes.add(new SizeData("A", "a.file", 30, 20, 10));
    assertEquals(1, sizes.size());
  }
}
