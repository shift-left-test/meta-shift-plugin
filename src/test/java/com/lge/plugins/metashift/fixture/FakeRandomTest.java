/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
