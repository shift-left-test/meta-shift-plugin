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
    dataList.add(new PassedTestData("A-B-C", "suite", "test", "msg"));
    assertEquals(1, dataList.size());
    assertTrue(dataList.contains(PassedTestData.class));
  }

  @Test
  public void testAddMultipleData() {
    dataList.add(new PassedTestData("A-B-C", "suite", "test", "msg"));
    dataList.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    assertEquals(2, dataList.size());
    assertTrue(dataList.contains(PassedTestData.class));
    assertTrue(dataList.contains(StatementCoverageData.class));
  }

  @Test
  public void testContainsWithInheritedData() {
    dataList.add(new KilledMutationTestData("A-B-C", "a.file", "X", "X", 1, "X", "X"));
    assertTrue(dataList.contains(KilledMutationTestData.class));
    assertTrue(dataList.contains(MutationTestData.class));
  }

  @Test
  public void testObjects() {
    dataList.add(new KilledMutationTestData("A-B-C", "a.file", "X", "X", 1, "X", "X"));
    dataList.add(new SurvivedMutationTestData("A-B-C", "b.file", "X", "X", 2, "X", "X"));
    dataList.add(new SkippedMutationTestData("A-B-C", "c.file", "X", "X", 3, "X", "X"));
    dataList.add(new PassedTestData("A-B-C", "suite", "test", "msg"));
    assertEquals(1, dataList.objects(KilledMutationTestData.class).count());
    assertEquals(3, dataList.objects(MutationTestData.class).count());
    assertEquals(4, dataList.objects(Data.class).count());
  }

  @Test
  public void testAddAllMultipleObjects() {
    List<Data> objects = Arrays.asList(
        new KilledMutationTestData("A-B-C", "a.file", "X", "X", 1, "X", "X"),
        new SurvivedMutationTestData("A-B-C", "b.file", "X", "X", 2, "X", "X"),
        new SkippedMutationTestData("A-B-C", "c.file", "X", "X", 3, "X", "X"),
        new PassedTestData("A-B-C", "suite", "test", "msg")
    );
    dataList.addAll(objects);
    assertEquals(1, dataList.objects(KilledMutationTestData.class).count());
    assertEquals(3, dataList.objects(MutationTestData.class).count());
    assertEquals(4, dataList.objects(Data.class).count());
  }
}
