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

/**
 * coverage for each file.
 */
public class FileCoverageTableItem implements Serializable {
  private final String file;
  private double lineCoverage;
  private double branchCoverage;

  /**
   * constructor.
   */
  public FileCoverageTableItem(String file, double lineCoverage, double branchCoverage) {
    this.file = file;
    this.lineCoverage = lineCoverage;
    this.branchCoverage = branchCoverage;
  }

  public String getFile() {
    return this.file;
  }

  public double getLineCoverage() {
    return this.lineCoverage;
  }

  public double getBranchCoverage() {
    return this.branchCoverage;
  }

  private static Comparator<FileCoverageTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileCoverageTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileCoverageTableItem::getFile);
        break;
      case "lineCoverage":
        comparator = Comparator.comparing(FileCoverageTableItem::getLineCoverage);
        break;
      case "branchCoverage":
        comparator = Comparator.comparing(FileCoverageTableItem::getBranchCoverage);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for coverage table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileCoverageTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileCoverageTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileCoverageTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
