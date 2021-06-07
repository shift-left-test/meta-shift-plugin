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
 * complexity for each file.
 */
public class FileComplexityTableItem implements Serializable {
  String file;
  long functions;
  long complexFunctions;

  /**
   * constructor.
   */
  public FileComplexityTableItem(String file, long functions, long complexFunctions) {
    this.file = file;
    this.functions = functions;
    this.complexFunctions = complexFunctions;
  }

  public String getFile() {
    return this.file;
  }

  public long getFunctions() {
    return this.functions;
  }

  public long getComplexFunctions() {
    return this.complexFunctions;
  }

  private static Comparator<FileComplexityTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileComplexityTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileComplexityTableItem::getFile);
        break;
      case "functions":
        comparator = Comparator.comparing(FileComplexityTableItem::getFunctions);
        break;
      case "complexFunctions":
        comparator = Comparator.comparing(FileComplexityTableItem::getComplexFunctions);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for complexity table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileComplexityTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileComplexityTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileComplexityTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }  
}
