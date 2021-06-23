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
import java.util.List;
import java.util.Map;

/**
 * Test metrics sortableitem list class.
 */
public class TestSortableItemList
    extends SortableItemList<TestSortableItemList.Item> {
  private static final long serialVersionUID = 1L;
  private static final Map<String, Comparator<Item>> comparators;
  
  static {
    comparators = new HashMap<>();
    comparators.put("suite", Comparator.comparing(Item::getSuite));
    comparators.put("name", Comparator.comparing(Item::getName));
    comparators.put("status", Comparator.comparing(Item::getStatus));
    comparators.put("message", Comparator.comparing(Item::getMessage));
  }

  public TestSortableItemList(List<Item> items) {
    super(items);
  }

  protected Comparator<Item> createComparator(SortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for unit test table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<Item> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * test table item.
   */
  public static class Item implements Serializable {

    private static final long serialVersionUID = 8885990883871159178L;
    private final String suite;
    private final String name;
    private final String status;
    private final String message;

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
    public Item(TestData data) {
      this.suite = data.getSuite();
      this.name = data.getName();
      this.status = data.getStatus();
      this.message = data.getMessage();
    }
  }
}