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

import com.lge.plugins.metashift.models.DuplicationData;
import java.io.Serializable;
import java.util.Comparator;

/**
 * duplication for each file.
 */
public class FileDuplicationTableItem implements Serializable {
  private String file;
  private long lines;
  private long duplicatedLines;

  public String getFile() {
    return this.file;
  }

  public long getLines() {
    return this.lines;
  }

  public long getDuplicatedLines() {
    return this.duplicatedLines;
  }

  /**
   * constructor.
   */
  public FileDuplicationTableItem(DuplicationData data) {
    this.file = data.getFile();
    this.lines = data.getLines();
    this.duplicatedLines = data.getDuplicatedLines();
  }

  private static Comparator<FileDuplicationTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileDuplicationTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileDuplicationTableItem::getFile);
        break;
      case "lines":
        comparator = Comparator.comparing(FileDuplicationTableItem::getLines);
        break;
      case "duplicatedLines":
        comparator = Comparator.comparing(FileDuplicationTableItem::getDuplicatedLines);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for duplications table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileDuplicationTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileDuplicationTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileDuplicationTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
