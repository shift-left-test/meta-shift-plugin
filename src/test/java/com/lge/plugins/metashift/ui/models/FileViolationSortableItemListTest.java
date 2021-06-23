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

public class FileViolationSortableItemListTest {
  @Test
  public void testAddItem() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();

    assertEquals(0, itemList.getItems().size());

    itemList.addItem("test.c", 1, 2, 3);
    assertEquals(1, itemList.getItems().size());
    assertEquals("test.c", itemList.getItems().get(0).getFile());
    assertEquals(1, itemList.getItems().get(0).getMajor());
    assertEquals(2, itemList.getItems().get(0).getMinor());
    assertEquals(3, itemList.getItems().get(0).getInfo());
  }

  @Test
  public void testSortByFileAsc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
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
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
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
  public void testSortByMajorAsc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "major")
    });

    assertEquals(1, itemList.getItems().get(0).getMajor());
    assertEquals(2, itemList.getItems().get(1).getMajor());
    assertEquals(3, itemList.getItems().get(2).getMajor());
  }

  @Test
  public void testSortByMajorDesc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "major")
    });

    assertEquals(3, itemList.getItems().get(0).getMajor());
    assertEquals(2, itemList.getItems().get(1).getMajor());
    assertEquals(1, itemList.getItems().get(2).getMajor());
  }

  @Test
  public void testSortByMinorAsc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "minor")
    });

    assertEquals(1, itemList.getItems().get(0).getMinor());
    assertEquals(2, itemList.getItems().get(1).getMinor());
    assertEquals(3, itemList.getItems().get(2).getMinor());
  }

  @Test
  public void testSortByMinorDesc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "minor")
    });

    assertEquals(3, itemList.getItems().get(0).getMinor());
    assertEquals(2, itemList.getItems().get(1).getMinor());
    assertEquals(1, itemList.getItems().get(2).getMinor());
  }

  @Test
  public void testSortByInfoAsc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("asc", "info")
    });

    assertEquals(1, itemList.getItems().get(0).getInfo());
    assertEquals(2, itemList.getItems().get(1).getInfo());
    assertEquals(3, itemList.getItems().get(2).getInfo());
  }

  @Test
  public void testSortByInfoDesc() {
    FileViolationSortableItemList itemList = new FileViolationSortableItemList();
    itemList.addItem("test2.c", 3, 2, 1);
    itemList.addItem("test1.c", 1, 3, 3);
    itemList.addItem("test3.c", 2, 1, 2);

    itemList.sort(new SortableItemList.SortInfo [] {
      new SortableItemList.SortInfo("desc", "info")
    });

    assertEquals(3, itemList.getItems().get(0).getInfo());
    assertEquals(2, itemList.getItems().get(1).getInfo());
    assertEquals(1, itemList.getItems().get(2).getInfo());
  }
}
