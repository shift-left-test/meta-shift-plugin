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
    second = new LinesOfCode(1, 2, 3, 4);
  }

  private void assertValues(LinesOfCode object, long lines, long functions, long classes,
      long files, long recipes) {
    assertEquals((Long) lines, object.getLines().getValue());
    assertEquals((Long) functions, object.getFunctions().getValue());
    assertEquals((Long) classes, object.getClasses().getValue());
    assertEquals((Long) files, object.getFiles().getValue());
    assertEquals((Long) recipes, object.getRecipes().getValue());
  }

  private void assertDifferences(LinesOfCode object, long lines, long functions, long classes,
      long files, long recipes) {
    assertEquals((Long) lines, object.getLines().getDifference());
    assertEquals((Long) functions, object.getFunctions().getDifference());
    assertEquals((Long) classes, object.getClasses().getDifference());
    assertEquals((Long) files, object.getFiles().getDifference());
    assertEquals((Long) recipes, object.getRecipes().getDifference());
  }

  @Test
  public void testInitialState() {
    assertValues(first, 11, 22, 33, 44, 55);
    assertDifferences(first, 0, 0, 0, 0, 0);

    assertValues(second, 1, 2, 3, 4, 0);
    assertDifferences(second, 0, 0, 0, 0, 0);
  }

  @Test
  public void testCopyConstructor() {
    LinesOfCode object = new LinesOfCode(first);
    assertValues(object, 11, 22, 33, 44, 55);
    assertDifferences(object, 0, 0, 0, 0, 0);
  }

  @Test
  public void testSetDifferenceWithSelf() {
    first.setDifference(first);
    assertValues(first, 11, 22, 33, 44, 55);
    assertDifferences(first, 0, 0, 0, 0, 0);
  }

  @Test
  public void testSetDifferenceWithLarge() {
    first.setDifference(second);
    assertValues(first, 11, 22, 33, 44, 55);
    assertDifferences(first, 10, 20, 30, 40, 55);
  }

  @Test
  public void testSetDifferenceWithSmall() {
    second.setDifference(first);
    assertValues(second, 1, 2, 3, 4, 0);
    assertDifferences(second, -10, -20, -30, -40, -55);
  }
}
