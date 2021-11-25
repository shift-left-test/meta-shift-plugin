/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Distribution class.
 *
 * @author Sung Gon Kim
 */
public class DistributionTest {

  private Distribution pair;
  private Distribution triplet;
  private Distribution quartet;

  @Before
  public void setUp() {
    pair = new Distribution(1, 0);
    triplet = new Distribution(2, 1, 0);
    quartet = new Distribution(3, 2, 1, 0);
  }

  private void assertValues(Scale o, long numerator, double ratio) {
    assertEquals(numerator, o.getCount());
    assertEquals(ratio, o.getRatio(), 0.01);
  }

  @Test
  public void testCreatePairObject() {
    assertValues(pair.getFirst(), 1, 1.0);
    assertValues(pair.getSecond(), 0, 0.0);
    assertValues(pair.getThird(), 0, 0.0);
    assertValues(pair.getFourth(), 0, 0.0);
    assertEquals(1, pair.getTotal());
  }

  @Test
  public void testCopyPairObject() {
    Distribution copied = new Distribution(pair);
    assertValues(copied.getFirst(), 1, 1.0);
    assertValues(copied.getSecond(), 0, 0.0);
    assertValues(copied.getThird(), 0, 0.0);
    assertValues(copied.getFourth(), 0, 0.0);
    assertEquals(1, copied.getTotal());
  }

  @Test
  public void testCreateTripletObject() {
    assertValues(triplet.getFirst(), 2, 0.66);
    assertValues(triplet.getSecond(), 1, 0.33);
    assertValues(triplet.getThird(), 0, 0.0);
    assertValues(triplet.getFourth(), 0, 0.0);
    assertEquals(3, triplet.getTotal());
  }

  @Test
  public void testCopyTripletObject() {
    Distribution copied = new Distribution(triplet);
    assertValues(triplet.getFirst(), 2, 0.66);
    assertValues(triplet.getSecond(), 1, 0.33);
    assertValues(triplet.getThird(), 0, 0.0);
    assertValues(triplet.getFourth(), 0, 0.0);
    assertEquals(3, triplet.getTotal());
  }

  @Test
  public void testCreateQuartetObject() {
    assertValues(quartet.getFirst(), 3, 0.5);
    assertValues(quartet.getSecond(), 2, 0.33);
    assertValues(quartet.getThird(), 1, 0.16);
    assertValues(quartet.getFourth(), 0, 0.0);
    assertEquals(6, quartet.getTotal());
  }

  @Test
  public void testCopyQuartetObject() {
    Distribution copied = new Distribution(quartet);
    assertValues(quartet.getFirst(), 3, 0.5);
    assertValues(quartet.getSecond(), 2, 0.33);
    assertValues(quartet.getThird(), 1, 0.16);
    assertValues(quartet.getFourth(), 0, 0.0);
    assertEquals(6, quartet.getTotal());
  }
}
