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

import com.lge.plugins.metashift.models.CommentData;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * comment for each file.
 */
public class FileCommentTableItem implements Serializable {

  private static final long serialVersionUID = -5041608534135525261L;
  private static final Map<String, Comparator<FileCommentTableItem>> comparators;
  private final String file;
  private final long lines;
  private final long commentLines;
  private final double commentRate;

  static {
    comparators = new HashMap<>();
    comparators.put("file", Comparator.comparing(FileCommentTableItem::getFile));
    comparators.put("lines", Comparator.comparing(FileCommentTableItem::getLines));
    comparators.put("commentLines", Comparator.comparing(FileCommentTableItem::getCommentLines));
    comparators.put("commentRate", Comparator.comparing(FileCommentTableItem::getCommentRate));
  }

  /**
   * constructor.
   */
  public FileCommentTableItem(CommentData data) {
    file = data.getFile();
    lines = data.getLines();
    commentLines = data.getCommentLines();
    commentRate = (double) commentLines / (double) lines;
  }

  public String getFile() {
    return file;
  }

  public long getLines() {
    return lines;
  }

  public long getCommentLines() {
    return commentLines;
  }

  public double getCommentRate() {
    return commentRate;
  }

  private static Comparator<FileCommentTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for comments table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<FileCommentTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for FileCommentTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileCommentTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<FileCommentTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}