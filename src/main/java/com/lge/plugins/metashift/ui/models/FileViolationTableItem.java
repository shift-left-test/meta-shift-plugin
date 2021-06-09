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
 * violation stats for each file.
 */
public class FileViolationTableItem implements Serializable {

  private static final long serialVersionUID = -8604715878738466132L;
  private static final Map<String, Comparator<FileViolationTableItem>> comparators;
  private final String file;
  private final long major;
  private final long minor;
  private final long info;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileViolationTableItem::getFile));
    comparators.put("major", Comparator.comparing(FileViolationTableItem::getMajor));
    comparators.put("minor", Comparator.comparing(FileViolationTableItem::getMinor));
    comparators.put("info", Comparator.comparing(FileViolationTableItem::getInfo));
  }

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
    return file;
  }

  public long getMajor() {
    return major;
  }

  public long getMinor() {
    return minor;
  }

  public long getInfo() {
    return info;
  }

  private static Comparator<FileViolationTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for recipe violations table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileViolationTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileViolationTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileViolationTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileViolationTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
