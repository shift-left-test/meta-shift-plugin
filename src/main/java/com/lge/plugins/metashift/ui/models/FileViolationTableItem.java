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
 * violation stats for each file.
 */
public class FileViolationTableItem implements Serializable {
  private final String file;
  private long major;
  private long minor;
  private long info;

  /**
   * constructor.
   */
  public FileViolationTableItem(String file, long major, long minor, long info) {
    this.file = file;
    this.major = major;
    this.minor = minor;
    this.info = info;
  }

  public String getFile() {
    return this.file;
  }

  public long getMajor() {
    return this.major;
  }

  public long getMinor() {
    return this.minor;
  }

  public long getInfo() {
    return this.info;
  }

  private static Comparator<FileViolationTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileViolationTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileViolationTableItem::getFile);
        break;
      case "major":
        comparator = Comparator.comparing(FileViolationTableItem::getMajor);
        break;
      case "minor":
        comparator = Comparator.comparing(FileViolationTableItem::getMinor);
        break;
      case "info":
        comparator = Comparator.comparing(FileViolationTableItem::getInfo);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for recipe violations table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileViolationTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileViolationTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileViolationTableItem> comparator =
        createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
