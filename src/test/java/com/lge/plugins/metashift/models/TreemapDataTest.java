/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.TreemapData.Grade;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TreemapData class.
 *
 * @author Sung Gon Kim
 */
public class TreemapDataTest {

  private static final double MAX = 6.0;

  private TreemapData object;

  @Before
  public void setUp() {
    object = new PositiveTreemapData("A-B-C", 123, 1.0, 1.0);
  }

  private void assertValues(TreemapData o, String name, long linesOfCode, double value,
      Grade grade) {
    assertEquals(name, o.getName());
    assertEquals(linesOfCode, o.getLinesOfCode());
    assertEquals(value, o.getValue(), 0.01);
    assertEquals(grade.ordinal(), o.getGrade());
  }

  private void assertPositiveGrade(double value, Grade grade) {
    TreemapData o = new PositiveTreemapData("", 0, MAX, value);
    assertEquals(grade.ordinal(), o.getGrade());
  }

  private void assertNegativeGrade(double value, Grade grade) {
    TreemapData o = new NegativeTreemapData("", 0, 0.0, MAX, value);
    assertEquals(grade.ordinal(), o.getGrade());
  }

  @Test
  public void testCreateObject() {
    assertValues(object, "A-B-C", 123, 1.0, Grade.BEST);
  }

  @Test
  public void testCreateObjectWithNegativeValue() {
    object = new PositiveTreemapData("X-X-X", 456, 1.0, -100.0);
    assertValues(object, "X-X-X", 456, 0.0, Grade.WORST);
  }

  @Test
  public void testPositiveTreeMapDataWithNegativeValue() {
    assertPositiveGrade(-1.0, Grade.WORST);
  }

  @Test
  public void testPositiveTreemapDataOfWorstGrade() {
    assertPositiveGrade(0.0, Grade.WORST);
  }

  @Test
  public void testPositiveTreemapDataOfWorseGrade() {
    assertPositiveGrade(1.0, Grade.WORSE);
  }

  @Test
  public void testPositiveTreemapDataOfBadGrade() {
    assertPositiveGrade(2.0, Grade.BAD);
  }

  @Test
  public void testPositiveTreemapDataOfOrdinaryGrade() {
    assertPositiveGrade(3.0, Grade.ORDINARY);
  }

  @Test
  public void testPositiveTreemapDataOfGoodGrade() {
    assertPositiveGrade(4.0, Grade.GOOD);
  }

  @Test
  public void testPositiveTreemapDataOfBetterGrade() {
    assertPositiveGrade(5.0, Grade.BETTER);
  }

  @Test
  public void testPositiveTreemapDataOfBestGrade() {
    assertPositiveGrade(6.0, Grade.BEST);
  }

  @Test
  public void testPositiveTreemapDataOfMaxValue() {
    assertPositiveGrade(7.0, Grade.BEST);
  }

  @Test
  public void testNegativeTreeMapDataWithNegativeValue() {
    assertNegativeGrade(-1.0, Grade.BEST);
  }

  @Test
  public void testNegativeTreemapDataOfBestGrade() {
    assertNegativeGrade(0.0, Grade.BEST);
  }

  @Test
  public void testNegativeTreemapDataOfBetterGrade() {
    assertNegativeGrade(1.0, Grade.BETTER);
  }

  @Test
  public void testNegativeTreemapDataOfGoodGrade() {
    assertNegativeGrade(2.0, Grade.GOOD);
  }

  @Test
  public void testNegativeTreemapDataOfOrdinaryGrade() {
    assertNegativeGrade(3.0, Grade.ORDINARY);
  }

  @Test
  public void testNegativeTreemapDataOfBadGrade() {
    assertNegativeGrade(4.0, Grade.BAD);
  }

  @Test
  public void testNegativeTreemapDataOfWorseGrade() {
    assertNegativeGrade(5.0, Grade.WORSE);
  }

  @Test
  public void testNegativeTreemapDataOfWorstGrade() {
    assertNegativeGrade(6.0, Grade.WORST);
  }

  @Test
  public void testNegativeTreemapDataOfMaxValue() {
    assertNegativeGrade(7.0, Grade.WORST);
  }
}
