/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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

  @Test
  public void testGetDifference() {
    assertValues(first.getDifference(second), 10, 20, 30, 40, 55);
    assertValues(second.getDifference(first), -10, -20, -30, -40, -55);
  }

  @Test
  public void testGetDifferenceWithNull() {
    assertValues(first.getDifference(null), 11, 22, 33, 44, 55);
    assertValues(second.getDifference(null), 1, 2, 3, 4, 0);
  }
}
