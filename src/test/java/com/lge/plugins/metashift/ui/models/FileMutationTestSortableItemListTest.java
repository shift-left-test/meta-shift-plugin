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
import org.junit.Test;

public class FileMutationTestSortableItemListTest {
  @Test
  public void testAddItem() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();

    assertEquals(0, itemList.getItems().size());

    itemList.addItem("test.c", 1, 2, 3);
    assertEquals(1, itemList.getItems().size());
    assertEquals("test.c", itemList.getItems().get(0).getFile());
    assertEquals(1, itemList.getItems().get(0).getKilled());
    assertEquals(2, itemList.getItems().get(0).getSurvived());
    assertEquals(3, itemList.getItems().get(0).getSkipped());
  }

  @Test
  public void testSortByFileAsc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "file")
    });

    assertEquals("test1.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test3.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByFileDesc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "file")
    });

    assertEquals("test3.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test1.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByKilledAsc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "killed")
    });

    assertEquals(1, itemList.getItems().get(0).getKilled());
    assertEquals(2, itemList.getItems().get(1).getKilled());
    assertEquals(3, itemList.getItems().get(2).getKilled());
  }

  @Test
  public void testSortByKilledDesc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "killed")
    });

    assertEquals(3, itemList.getItems().get(0).getKilled());
    assertEquals(2, itemList.getItems().get(1).getKilled());
    assertEquals(1, itemList.getItems().get(2).getKilled());
  }

  @Test
  public void testSortBySurvivedAsc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "survived")
    });

    assertEquals(1, itemList.getItems().get(0).getSurvived());
    assertEquals(2, itemList.getItems().get(1).getSurvived());
    assertEquals(3, itemList.getItems().get(2).getSurvived());
  }

  @Test
  public void testSortBySurvivedDesc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "survived")
    });

    assertEquals(3, itemList.getItems().get(0).getSurvived());
    assertEquals(2, itemList.getItems().get(1).getSurvived());
    assertEquals(1, itemList.getItems().get(2).getSurvived());
  }

  @Test
  public void testSortBySkippedAsc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "skipped")
    });

    assertEquals(1, itemList.getItems().get(0).getSkipped());
    assertEquals(2, itemList.getItems().get(1).getSkipped());
    assertEquals(3, itemList.getItems().get(2).getSkipped());
  }

  @Test
  public void testSortBySkippedDesc() {
    FileMutationTestSortableItemList itemList = new FileMutationTestSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "skipped")
    });

    assertEquals(3, itemList.getItems().get(0).getSkipped());
    assertEquals(2, itemList.getItems().get(1).getSkipped());
    assertEquals(1, itemList.getItems().get(2).getSkipped());
  }
}
