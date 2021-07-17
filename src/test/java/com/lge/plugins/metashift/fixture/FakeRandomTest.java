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

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests for the FakeRandom class.
 *
 * @author Sung Gon Kim
 */
public class FakeRandomTest {

  private static final int MAX_ELEMENTS = 100000;

  @Test
  public void testUniqueStrings() {
    Set<String> strings = new HashSet<>();
    for (int i = 0; i < MAX_ELEMENTS; i++) {
      strings.add(FakeRandom.nextString());
    }
    assertEquals(MAX_ELEMENTS, strings.size());
  }

  @Test
  public void testUniqueNumbers() {
    Set<Integer> numbers = new HashSet<>();
    for (int i = 0; i < MAX_ELEMENTS; i++) {
      numbers.add(FakeRandom.nextNumber());
    }
    assertEquals(MAX_ELEMENTS, numbers.size());
  }
}
