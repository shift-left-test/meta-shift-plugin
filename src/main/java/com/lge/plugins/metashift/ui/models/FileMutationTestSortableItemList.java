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
 * Mutation test metrics sortableitem list class.
 */
public class FileMutationTestSortableItemList
    extends SortableItemList<FileMutationTestSortableItemList.Item> {
  private static final long serialVersionUID = 1L;
  private static final Map<String, Comparator<Item>> comparators;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(Item::getFile));
    comparators.put("killed", Comparator.comparing(Item::getKilled));
    comparators.put("survived", Comparator.comparing(Item::getSurvived));
    comparators.put("skipped", Comparator.comparing(Item::getSkipped));
  }

  public FileMutationTestSortableItemList() {
    super(new ArrayList<>());
  }

  public void addItem(String file, long killed, long survived, long skipped) {
    this.items.add(new Item(file, killed, survived, skipped));
  }

  protected Comparator<Item> createComparator(SortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for mutation test table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<Item> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }
  
  /**
   * mutation test for each file.
   */
  public static class Item implements Serializable {

    private static final long serialVersionUID = 6776920372143480891L;
    private final String file;
    private final long killed;
    private final long survived;
    private final long skipped;

    /**
     * constructor.
     */
    public Item(String file, long killed, long survived, long skipped) {
      this.file = file;
      this.killed = killed;
      this.survived = survived;
      this.skipped = skipped;
    }

    public String getFile() {
      return file;
    }

    public long getKilled() {
      return killed;
    }

    public long getSurvived() {
      return survived;
    }

    public long getSkipped() {
      return skipped;
    }
  }
}
