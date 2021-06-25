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

public class FileCoverageSortableItemListTest {

  @Test
  public void testAddItem() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();

    assertEquals(0, itemList.getItems().size());

    itemList.addItem("test.c", 0.1, 0.2);
    assertEquals(1, itemList.getItems().size());
    assertEquals("test.c", itemList.getItems().get(0).getFile());
    assertEquals(0.1, itemList.getItems().get(0).getLineCoverage(), 0);
    assertEquals(0.2, itemList.getItems().get(0).getBranchCoverage(), 0);
  }

  @Test
  public void testSortByFileAsc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "file")
    });

    assertEquals("test1.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test3.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByFileDesc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "file")
    });

    assertEquals("test3.c", itemList.getItems().get(0).getFile());
    assertEquals("test2.c", itemList.getItems().get(1).getFile());
    assertEquals("test1.c", itemList.getItems().get(2).getFile());
  }

  @Test
  public void testSortByLineCoverageAsc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "lineCoverage")
    });

    assertEquals(0.1, itemList.getItems().get(0).getLineCoverage(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getLineCoverage(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getLineCoverage(), 0);

  }

  @Test
  public void testSortByLineCoverageDesc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "lineCoverage")
    });

    assertEquals(0.3, itemList.getItems().get(0).getLineCoverage(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getLineCoverage(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getLineCoverage(), 0);
  }

  @Test
  public void testSortByBranchCoverageAsc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "branchCoverage")
    });

    assertEquals(0.1, itemList.getItems().get(0).getBranchCoverage(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getBranchCoverage(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getBranchCoverage(), 0);
  }

  @Test
  public void testSortByBranchCoverageDesc() {
    FileCoverageSortableItemList itemList = new FileCoverageSortableItemList();
    itemList.addItem("test2.c", 0.3, 0.2);
    itemList.addItem("test1.c", 0.1, 0.3);
    itemList.addItem("test3.c", 0.2, 0.1);

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "branchCoverage")
    });

    assertEquals(0.3, itemList.getItems().get(0).getBranchCoverage(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getBranchCoverage(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getBranchCoverage(), 0);
  }
}
