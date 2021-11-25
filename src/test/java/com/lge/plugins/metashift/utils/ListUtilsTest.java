/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ListUtils class.
 *
 * @author Sung Gon Kim
 */
public class ListUtilsTest {

  private List<Integer> objects;

  @Before
  public void setUp() {
    objects = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Test
  public void testPartitionWithNull() {
    assertEquals(Collections.emptyList(), ListUtils.partition(null, 10));
  }

  @Test
  public void testPartitionWithNegativeSize() {
    assertEquals(Collections.emptyList(), ListUtils.partition(objects, -1));
  }

  @Test
  public void testPartitionWithZeroSize() {
    assertEquals(Collections.emptyList(), ListUtils.partition(objects, 0));
  }

  @Test
  public void testPartitionWithHalve() {
    List<List<Integer>> lists = ListUtils.partition(objects, 5);
    assertEquals(2, lists.size());
    assertEquals(Arrays.asList(0, 1, 2, 3, 4), lists.get(0));
    assertEquals(Arrays.asList(5, 6, 7, 8, 9), lists.get(1));
  }

  @Test
  public void testPartitionWithFour() {
    List<List<Integer>> lists = ListUtils.partition(objects, 4);
    assertEquals(3, lists.size());
    assertEquals(Arrays.asList(0, 1, 2, 3), lists.get(0));
    assertEquals(Arrays.asList(4, 5, 6, 7), lists.get(1));
    assertEquals(Arrays.asList(8, 9), lists.get(2));
  }

  @Test
  public void testPartitionWithOne() {
    List<List<Integer>> lists = ListUtils.partition(objects, 1);
    assertEquals(10, lists.size());
  }

  @Test
  public void testPartitionWithLargeValue() {
    List<List<Integer>> lists = ListUtils.partition(objects, objects.size() + 1);
    assertEquals(1, lists.size());
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), lists.get(0));
  }

  @Test
  public void testPartitionWithMaxValue() {
    List<List<Integer>> lists = ListUtils.partition(objects, Integer.MAX_VALUE);
    assertEquals(1, lists.size());
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), lists.get(0));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testPartitionWithNegativeIndex() {
    List<List<Integer>> lists = ListUtils.partition(objects, 5);
    List<Integer> subList = lists.get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testPartitionWithOutOfBoundsIndex() {
    List<List<Integer>> lists = ListUtils.partition(objects, 5);
    List<Integer> subList = lists.get(lists.size() + 1);
  }
}
