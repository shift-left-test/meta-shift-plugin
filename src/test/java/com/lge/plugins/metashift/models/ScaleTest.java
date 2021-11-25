/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Scale class.
 *
 * @author Sung Gon Kim
 */
public class ScaleTest {

  private Scale object;

  @Before
  public void setUp() {
    object = new Scale(10, 5);
  }

  private void assertValues(Scale o, long numerator, double ratio) {
    assertEquals(numerator, o.getCount());
    assertEquals(ratio, o.getRatio(), 0.01);
  }

  @Test
  public void testCreateObject() {
    assertValues(object, 5, 0.5);
  }

  @Test
  public void testCopyConstructor() {
    Scale copied = new Scale(object);
    assertValues(copied, 5, 0.5);
  }

  @Test
  public void testCreateObjectWithZeroDenominator() {
    object = new Scale(0, 1);
    assertValues(object, 1, 0.0);
  }
}
