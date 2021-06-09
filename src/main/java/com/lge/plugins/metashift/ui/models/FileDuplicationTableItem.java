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
import java.util.HashMap;
import java.util.Map;

/**
 * duplication for each file.
 */
public class FileDuplicationTableItem implements Serializable {

  private static final long serialVersionUID = -2150956341341431329L;
  private static final Map<String, Comparator<FileDuplicationTableItem>> comparators;
  private final String file;
  private final long lines;
  private final long duplicatedLines;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileDuplicationTableItem::getFile));
    comparators.put("lines", Comparator.comparing(FileDuplicationTableItem::getLines));
    comparators.put("duplicatedLines",
        Comparator.comparing(FileDuplicationTableItem::getDuplicatedLines));
  }

  public String getFile() {
    return file;
  }

  public long getLines() {
    return lines;
  }

  public long getDuplicatedLines() {
    return duplicatedLines;
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
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for duplications table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileDuplicationTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileDuplicationTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileDuplicationTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileDuplicationTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
