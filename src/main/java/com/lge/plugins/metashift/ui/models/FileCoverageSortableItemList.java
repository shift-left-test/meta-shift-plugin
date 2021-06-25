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
 * Coverage metrics sortableitem list class.
 */
public class FileCoverageSortableItemList
    extends SortableItemList<FileCoverageSortableItemList.Item> {

  private static final long serialVersionUID = 1L;
  private static final Map<String, Comparator<Item>> comparators;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(Item::getFile));
    comparators.put("lineCoverage", Comparator.comparing(Item::getLineCoverage));
    comparators.put("branchCoverage",
        Comparator.comparing(Item::getBranchCoverage));
  }

  public FileCoverageSortableItemList() {
    super(new ArrayList<>());
  }

  public void addItem(String file, double lineCoverage, double branchCoverage) {
    this.items.add(new Item(file, lineCoverage, branchCoverage));
  }

  protected Comparator<Item> createComparator(SortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for coverage table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<Item> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * coverage for each file.
   */
  public static class Item implements Serializable {

    private static final long serialVersionUID = -7175754091249866685L;
    private final String file;
    private final double lineCoverage;
    private final double branchCoverage;

    /**
     * constructor.
     */
    public Item(String file, double lineCoverage, double branchCoverage) {
      this.file = file;
      this.lineCoverage = lineCoverage;
      this.branchCoverage = branchCoverage;
    }

    public String getFile() {
      return file;
    }

    public double getLineCoverage() {
      return lineCoverage;
    }

    public double getBranchCoverage() {
      return branchCoverage;
    }
  }
}
