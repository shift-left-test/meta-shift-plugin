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

package com.lge.plugins.metashift.ui.models;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.TestData;

import org.junit.Before;
import org.junit.Test;

public class TestSortableItemListTest {
  private List<TestData> testDataList;

  @Before
  public void setUp() {
    testDataList = Arrays.asList(new TestData [] {
      new PassedTestData("test-0-0", "stest1", "test2", "message3"),
      new PassedTestData("test-0-0", "stest3", "test3", "message1"),
      new FailedTestData("test-0-0", "stest2", "test1", "message2"),
    });
  }

  @Test
  public void testSortBySuiteAsc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "suite")
    });

    assertEquals("stest1", itemList.getItems().get(0).getSuite());
    assertEquals("stest2", itemList.getItems().get(1).getSuite());
    assertEquals("stest3", itemList.getItems().get(2).getSuite());
  }

  @Test
  public void testSortBySuiteDesc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "suite")
    });

    assertEquals("stest3", itemList.getItems().get(0).getSuite());
    assertEquals("stest2", itemList.getItems().get(1).getSuite());
    assertEquals("stest1", itemList.getItems().get(2).getSuite());
  }

  @Test
  public void testSortByNameAsc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "name")
    });

    assertEquals("test1", itemList.getItems().get(0).getName());
    assertEquals("test2", itemList.getItems().get(1).getName());
    assertEquals("test3", itemList.getItems().get(2).getName());
  }

  @Test
  public void testSortByNameDesc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "name")
    });

    assertEquals("test3", itemList.getItems().get(0).getName());
    assertEquals("test2", itemList.getItems().get(1).getName());
    assertEquals("test1", itemList.getItems().get(2).getName());
  }

  @Test
  public void testSortByStatusAsc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "status")
    });

    assertEquals("FAILED", itemList.getItems().get(0).getStatus());
    assertEquals("PASSED", itemList.getItems().get(1).getStatus());
    assertEquals("PASSED", itemList.getItems().get(2).getStatus());
  }

  @Test
  public void testSortByStatusDesc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "status")
    });

    assertEquals("PASSED", itemList.getItems().get(0).getStatus());
    assertEquals("PASSED", itemList.getItems().get(1).getStatus());
    assertEquals("FAILED", itemList.getItems().get(2).getStatus());
  }

  @Test
  public void testSortByMessageAsc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "message")
    });

    assertEquals("message1", itemList.getItems().get(0).getMessage());
    assertEquals("message2", itemList.getItems().get(1).getMessage());
    assertEquals("message3", itemList.getItems().get(2).getMessage());
  }

  @Test
  public void testSortByMessageDesc() {
    List<TestSortableItemList.Item> dataList = testDataList.stream()
    .map(o -> new TestSortableItemList.Item(o)).collect(Collectors.toList());

    TestSortableItemList itemList = new TestSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "message")
    });

    assertEquals("message3", itemList.getItems().get(0).getMessage());
    assertEquals("message2", itemList.getItems().get(1).getMessage());
    assertEquals("message1", itemList.getItems().get(2).getMessage());
  }
}
