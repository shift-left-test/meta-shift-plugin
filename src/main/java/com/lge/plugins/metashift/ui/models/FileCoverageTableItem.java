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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * coverage for each file.
 */
public class FileCoverageTableItem implements Serializable {

  private static final long serialVersionUID = -7175754091249866685L;
  private static final Map<String, Comparator<FileCoverageTableItem>> comparators;
  private final String file;
  private final double lineCoverage;
  private final double branchCoverage;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileCoverageTableItem::getFile));
    comparators.put("lineCoverage", Comparator.comparing(FileCoverageTableItem::getLineCoverage));
    comparators.put("branchCoverage",
        Comparator.comparing(FileCoverageTableItem::getBranchCoverage));
  }

  /**
   * constructor.
   */
  public FileCoverageTableItem(String file, double lineCoverage, double branchCoverage) {
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

  private static Comparator<FileCoverageTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for coverage table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileCoverageTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileCoverageTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileCoverageTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileCoverageTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
