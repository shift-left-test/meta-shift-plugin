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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Guarantees to generate unique numbers and strings.
 *
 * @author Sung Gon Kim
 */
public class FakeRandom {

  private static final int MAX_ELEMENTS = 200000;

  private static List<String> strings = new ArrayList<>();
  private static int stringIndex = 0;
  private static int numberIndex = 0;

  static {
    for (int i = 0; i < MAX_ELEMENTS; i++) {
      strings.add(RandomStringUtils.randomAlphabetic(10));
    }
    strings = strings.stream().distinct().collect(Collectors.toList());
  }

  public static String nextString() {
    if (stringIndex + 1 > strings.size()) {
      stringIndex = 0;
    }
    return strings.get(stringIndex++);
  }

  public static int nextNumber() {
    return numberIndex++;
  }
}
