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
 * Unit tests for the LinesOfCode class.
 *
 * @author Sung Gon Kim
 */
public class LinesOfCodeTest {

  private LinesOfCode first;
  private LinesOfCode second;

  @Before
  public void setUp() {
    first = new LinesOfCode(11, 22, 33, 44, 55);
    second = new LinesOfCode(1, 2, 3, 4, 0);
  }

  private void assertValues(LinesOfCode object, long lines, long functions, long classes,
      long files, long recipes) {
    assertEquals(lines, object.getLines());
    assertEquals(functions, object.getFunctions());
    assertEquals(classes, object.getClasses());
    assertEquals(files, object.getFiles());
    assertEquals(recipes, object.getRecipes());
  }

  @Test
  public void testInitialState() {
    assertValues(first, 11, 22, 33, 44, 55);
    assertValues(second, 1, 2, 3, 4, 0);
  }

  @Test
  public void testCopyConstructor() {
    LinesOfCode object = new LinesOfCode(first);
    assertValues(object, 11, 22, 33, 44, 55);
  }
}
