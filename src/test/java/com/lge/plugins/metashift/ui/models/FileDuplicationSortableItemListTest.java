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

import com.lge.plugins.metashift.models.DuplicationData;

import org.junit.Before;
import org.junit.Test;

public class FileDuplicationSortableItemListTest {
  private List<DuplicationData> duplicationDataList;

  @Before
  public void setUp() {
    duplicationDataList = Arrays.asList(new DuplicationData [] {
        new DuplicationData("test-0-0", "test2.c", 200, 30),
        new DuplicationData("test-0-0", "test3.c", 100, 10),
        new DuplicationData("test-0-0", "test1.c", 300, 20)
    });
  }

  @Test
  public void testSortByFileAsc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "file")
    });

    assertEquals("test1.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test3.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByFileDesc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "file")
    });

    assertEquals("test3.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test1.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByLinesAsc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "lines")
    });

    assertEquals(100, itemList.getItems().get(0).getLines());
    assertEquals(200, itemList.getItems().get(1).getLines());
    assertEquals(300, itemList.getItems().get(2).getLines());
  }

  @Test
  public void testSortByLinesDesc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "lines")
    });

    assertEquals(300, itemList.getItems().get(0).getLines());
    assertEquals(200, itemList.getItems().get(1).getLines());
    assertEquals(100, itemList.getItems().get(2).getLines());
  }

  @Test
  public void testSortByDuplicatedLinesAsc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "duplicatedLines")
    });

    assertEquals(10, itemList.getItems().get(0).getDuplicatedLines());
    assertEquals(20, itemList.getItems().get(1).getDuplicatedLines());
    assertEquals(30, itemList.getItems().get(2).getDuplicatedLines());
  }

  @Test
  public void testSortByDupplcatedLinesDesc() {
    List<FileDuplicationSortableItemList.Item> dataList = duplicationDataList.stream()
        .map(o -> new FileDuplicationSortableItemList.Item(o)).collect(Collectors.toList());

    FileDuplicationSortableItemList itemList = new FileDuplicationSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "duplicatedLines")
    });

    assertEquals(30, itemList.getItems().get(0).getDuplicatedLines());
    assertEquals(20, itemList.getItems().get(1).getDuplicatedLines());
    assertEquals(10, itemList.getItems().get(2).getDuplicatedLines());
  }
}
