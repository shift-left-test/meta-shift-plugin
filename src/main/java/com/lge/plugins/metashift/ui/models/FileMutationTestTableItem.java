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
 * mutation test for each file.
 */
public class FileMutationTestTableItem implements Serializable {
  private String file;
  private long killed;
  private long survived;
  private long skipped;

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
    return this.file;
  }

  public long getKilled() {
    return this.killed;
  }

  public long getSurvived() {
    return this.survived;
  }

  public long getSkipped() {
    return this.skipped;
  }

  private static Comparator<FileMutationTestTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileMutationTestTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileMutationTestTableItem::getFile);
        break;
      case "killed":
        comparator = Comparator.comparing(FileMutationTestTableItem::getKilled);
        break;
      case "survived":
        comparator = Comparator.comparing(FileMutationTestTableItem::getSurvived);
        break;
      case "skipped":
        comparator = Comparator.comparing(FileMutationTestTableItem::getSkipped);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for mutation test table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileMutationTestTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileMutationTestTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileMutationTestTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
