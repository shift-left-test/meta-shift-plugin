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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Complexity metrics sortableitem list class.
 */
public class FileComplexitySortableItemList
    extends SortableItemList<FileComplexitySortableItemList.Item> {

  private static final long serialVersionUID = 1L;
  private static final Map<String, Comparator<Item>> comparators;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(Item::getFile));
    comparators.put("functions", Comparator.comparing(Item::getFunctions));
    comparators.put("complexFunctions",
        Comparator.comparing(Item::getComplexFunctions));
  }

  public FileComplexitySortableItemList() {
    super(new ArrayList<>());
  }

  public void addItem(String file, long functions, long complexFunctions) {
    this.items.add(new Item(file, functions, complexFunctions));
  }

  protected Comparator<Item> createComparator(SortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for complexity table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<Item> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * complexity for each file.
   */
  public static class Item implements Serializable {

    private static final long serialVersionUID = 8975343139553009115L;
    String file;
    long functions;
    long complexFunctions;

    /**
     * constructor.
     */
    public Item(String file, long functions, long complexFunctions) {
      this.file = file;
      this.functions = functions;
      this.complexFunctions = complexFunctions;
    }

    public String getFile() {
      return file;
    }

    public long getFunctions() {
      return functions;
    }

    public long getComplexFunctions() {
      return complexFunctions;
    }
  }
}
