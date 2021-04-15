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
