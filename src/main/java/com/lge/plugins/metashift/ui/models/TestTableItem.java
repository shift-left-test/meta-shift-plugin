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
import java.util.HashMap;
import java.util.Map;

/**
 * test table item.
 */
public class TestTableItem implements Serializable {

  private static final long serialVersionUID = 8885990883871159178L;
  private static final Map<String, Comparator<TestTableItem>> comparators;
  private final String suite;
  private final String name;
  private final String status;
  private final String message;

  static {
    comparators = new HashMap<>();
    comparators.put("suite", Comparator.comparing(TestTableItem::getSuite));
    comparators.put("name", Comparator.comparing(TestTableItem::getName));
    comparators.put("status", Comparator.comparing(TestTableItem::getStatus));
    comparators.put("message", Comparator.comparing(TestTableItem::getMessage));
  }

  public String getSuite() {
    return suite;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
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
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for unit test table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<TestTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for TestTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<TestTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<TestTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
