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
 * complexity for each file.
 */
public class FileComplexityTableItem implements Serializable {

  private static final long serialVersionUID = 8975343139553009115L;
  private static final Map<String, Comparator<FileComplexityTableItem>> comparators;
  String file;
  long functions;
  long complexFunctions;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileComplexityTableItem::getFile));
    comparators.put("functions", Comparator.comparing(FileComplexityTableItem::getFunctions));
    comparators.put("complexFunctions",
        Comparator.comparing(FileComplexityTableItem::getComplexFunctions));
  }

  /**
   * constructor.
   */
  public FileComplexityTableItem(String file, long functions, long complexFunctions) {
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

  private static Comparator<FileComplexityTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for complexity table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileComplexityTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileComplexityTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileComplexityTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileComplexityTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
