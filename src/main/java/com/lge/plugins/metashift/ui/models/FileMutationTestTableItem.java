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
 * mutation test for each file.
 */
public class FileMutationTestTableItem implements Serializable {

  private static final long serialVersionUID = 6776920372143480891L;
  private static final Map<String, Comparator<FileMutationTestTableItem>> comparators;
  private final String file;
  private final long killed;
  private final long survived;
  private final long skipped;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileMutationTestTableItem::getFile));
    comparators.put("killed", Comparator.comparing(FileMutationTestTableItem::getKilled));
    comparators.put("survived", Comparator.comparing(FileMutationTestTableItem::getSurvived));
    comparators.put("skipped", Comparator.comparing(FileMutationTestTableItem::getSkipped));
  }

  /**
   * constructor.
   */
  public FileMutationTestTableItem(String file, long killed, long survived, long skipped) {
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

  private static Comparator<FileMutationTestTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for mutation test table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileMutationTestTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileMutationTestTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileMutationTestTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileMutationTestTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
