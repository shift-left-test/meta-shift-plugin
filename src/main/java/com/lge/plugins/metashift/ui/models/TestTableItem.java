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

import com.lge.plugins.metashift.models.TestData;
import java.io.Serializable;
import java.util.Comparator;

/**
 * test table item.
 */
public class TestTableItem implements Serializable {
  private String suite;
  private String name;
  private String status;
  private String message;

  public String getSuite() {
    return this.suite;
  }

  public String getName() {
    return this.name;
  }

  public String getStatus() {
    return this.status;
  }

  public String getMessage() {
    return this.message;
  }

  /**
   * constructor.
   */
  public TestTableItem(TestData data) {
    this.suite = data.getSuite();
    this.name = data.getName();
    this.status = data.getStatus();
    this.message = data.getMessage();
  }

  private static Comparator<TestTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<TestTableItem> comparator;

    switch (sortInfo.getField()) {
      case "suite":
        comparator = Comparator.comparing(TestTableItem::getSuite);
        break;
      case "name":
        comparator = Comparator.comparing(TestTableItem::getName);
        break;
      case "status":
        comparator = Comparator.comparing(TestTableItem::getStatus);
        break;
      case "message":
        comparator = Comparator.comparing(TestTableItem::getMessage);
        break;
      default:
        throw new IllegalArgumentException(
          String.format("unknown field for unit test table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for TestTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<TestTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<TestTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
