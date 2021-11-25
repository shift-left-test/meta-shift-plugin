/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
