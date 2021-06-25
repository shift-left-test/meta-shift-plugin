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

public class FileComplexitySortableItemListTest {

  @Test
  public void testAddItem() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();

    assertEquals(itemList.getItems().size(), 0);

    itemList.addItem("test.c", 10, 5);
    assertEquals(1, itemList.getItems().size());
    assertEquals("test.c", itemList.getItems().get(0).getFile());
    assertEquals(10, itemList.getItems().get(0).getFunctions());
    assertEquals(5, itemList.getItems().get(0).getComplexFunctions());
  }

  @Test
  public void testSortByFileAsc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "file")
    });

    assertEquals("test1.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test3.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByFileDesc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "file")
    });

    assertEquals("test3.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test1.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByFunctionsAsc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "functions")
    });

    assertEquals(100, itemList.getItems().get(0).getFunctions());
    assertEquals(200, itemList.getItems().get(1).getFunctions());
    assertEquals(300, itemList.getItems().get(2).getFunctions());
  }

  @Test
  public void testSortByFunctionsDesc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "functions")
    });

    assertEquals(300, itemList.getItems().get(0).getFunctions());
    assertEquals(200, itemList.getItems().get(1).getFunctions());
    assertEquals(100, itemList.getItems().get(2).getFunctions());
  }

  @Test
  public void testSortByComplexFunctionsAsc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "complexFunctions")
    });

    assertEquals(10, itemList.getItems().get(0).getComplexFunctions());
    assertEquals(20, itemList.getItems().get(1).getComplexFunctions());
    assertEquals(30, itemList.getItems().get(2).getComplexFunctions());
  }

  @Test
  public void testSortByComplexFunctionsDesc() {
    FileComplexitySortableItemList itemList = new FileComplexitySortableItemList();
    itemList.addItem("test2.c", 300, 20);
    itemList.addItem("test1.c", 100, 30);
    itemList.addItem("test3.c", 200, 10);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "complexFunctions")
    });

    assertEquals(30, itemList.getItems().get(0).getComplexFunctions());
    assertEquals(20, itemList.getItems().get(1).getComplexFunctions());
    assertEquals(10, itemList.getItems().get(2).getComplexFunctions());
  }
}
