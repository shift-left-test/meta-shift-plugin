/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DataList class.
 *
 * @author Sung Gon Kim
 */
public class DataListTest {

  private DataList dataList;

  @Before
  public void setUp() {
    dataList = new DataList();
  }

  @Test
  public void testInitialState() {
    assertEquals(0, dataList.size());
  }

  @Test
  public void testAddSingleData() {
    dataList.add(new CodeSizeData("A-B-C", "a.file", 10, 1, 1));
    assertEquals(1, dataList.size());
    assertTrue(dataList.contains(CodeSizeData.class));
  }

  @Test
  public void testAddMultipleData() {
    dataList.add(new CodeSizeData("A-B-C", "a.file", 10, 1, 1));
    dataList.add(new PremirrorCacheData("A-B-C", "A:B", true));
    assertEquals(2, dataList.size());
    assertTrue(dataList.contains(CodeSizeData.class));
    assertTrue(dataList.contains(PremirrorCacheData.class));
  }

  @Test
  public void testContainsWithInheritedData() {
    dataList.add(new MajorCodeViolationData("A-B-C", "a.file", 1, 1, "r", "m", "d", "e", "t"));
    assertTrue(dataList.contains(MajorCodeViolationData.class));
    assertTrue(dataList.contains(CodeViolationData.class));
    assertTrue(dataList.contains(ViolationData.class));
  }

  @Test
  public void testObjects() {
    dataList.add(new MajorCodeViolationData("A-B-C", "a.file", 1, 1, "r", "m", "d", "e", "t"));
    dataList.add(new InfoCodeViolationData("A-B-C", "b.file", 1, 1, "r", "m", "d", "i", "t"));
    dataList.add(new MajorRecipeViolationData("A-B-C", "a.file", 1, "r", "d", "e"));
    dataList.add(new CodeSizeData("A-B-C", "a.file", 10, 1, 1));
    assertEquals(1, dataList.objects(MajorCodeViolationData.class).count());
    assertEquals(2, dataList.objects(CodeViolationData.class).count());
    assertEquals(3, dataList.objects(ViolationData.class).count());
    assertEquals(4, dataList.objects(Data.class).count());
  }

  @Test
  public void testAddAllMultipleObjects() {
    List<Data> objects = Arrays.asList(
        new MajorCodeViolationData("A-B-C", "a.file", 1, 1, "r", "m", "d", "e", "t"),
        new InfoCodeViolationData("A-B-C", "b.file", 1, 1, "r", "m", "d", "i", "t"),
        new MajorRecipeViolationData("A-B-C", "a.file", 1, "r", "d", "e"),
        new CodeSizeData("A-B-C", "a.file", 10, 1, 1)
    );
    dataList.addAll(objects);
    assertEquals(1, dataList.objects(MajorCodeViolationData.class).count());
    assertEquals(2, dataList.objects(CodeViolationData.class).count());
    assertEquals(3, dataList.objects(ViolationData.class).count());
    assertEquals(4, dataList.objects(Data.class).count());
  }
}
