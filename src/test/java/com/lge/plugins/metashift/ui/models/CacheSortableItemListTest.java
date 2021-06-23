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

import com.lge.plugins.metashift.models.PremirrorCacheData;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the CacheSortableItemList class.
 */
public class CacheSortableItemListTest {
  private List<PremirrorCacheData> cacheDataList;

  @Before
  public void setUp() {
    cacheDataList = Arrays.asList(new PremirrorCacheData [] {
        new PremirrorCacheData("test-0-0", "sig2", true),
        new PremirrorCacheData("test-0-0", "sig3", false),
        new PremirrorCacheData("test-0-0", "sig1", true)
    });
  }

  @Test
  public void testSortBySignatureAsc() {
    List<CacheSortableItemList.Item> dataList = cacheDataList.stream()
        .map(o -> new CacheSortableItemList.Item(o)).collect(Collectors.toList());

    CacheSortableItemList itemList = new CacheSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "signature")
    });

    assertEquals("sig1", itemList.getItems().get(0).getSignature());
    assertEquals("sig2", itemList.getItems().get(1).getSignature());
    assertEquals("sig3", itemList.getItems().get(2).getSignature());
  }

  @Test
  public void testSortBySignatureDesc() {
    List<CacheSortableItemList.Item> dataList = cacheDataList.stream()
        .map(o -> new CacheSortableItemList.Item(o)).collect(Collectors.toList());

    CacheSortableItemList itemList = new CacheSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "signature")
    });

    assertEquals("sig3", itemList.getItems().get(0).getSignature());
    assertEquals("sig2", itemList.getItems().get(1).getSignature());
    assertEquals("sig1", itemList.getItems().get(2).getSignature());
  }

  @Test
  public void testSortByAvailableAsc() {
    List<CacheSortableItemList.Item> dataList = cacheDataList.stream()
        .map(o -> new CacheSortableItemList.Item(o)).collect(Collectors.toList());

    CacheSortableItemList itemList = new CacheSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "available")
    });

    assertEquals(false, itemList.getItems().get(0).isAvailable());
    assertEquals(true, itemList.getItems().get(1).isAvailable());
    assertEquals(true, itemList.getItems().get(2).isAvailable());
  }

  @Test
  public void testSortByAvailableDesc() {
    List<CacheSortableItemList.Item> dataList = cacheDataList.stream()
        .map(o -> new CacheSortableItemList.Item(o)).collect(Collectors.toList());

    CacheSortableItemList itemList = new CacheSortableItemList(dataList);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "available")
    });

    assertEquals(true, itemList.getItems().get(0).isAvailable());
    assertEquals(true, itemList.getItems().get(1).isAvailable());
    assertEquals(false, itemList.getItems().get(2).isAvailable());
  }
}
