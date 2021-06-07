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

/**
 * comment for each file.
 */
public class FileCommentTableItem implements Serializable {
  private final String file;
  private final long lines;
  private final long commentLines;
  private final double commentRate;

  /**
   * constructor.
   */
  public FileCommentTableItem(CommentData data) {
    this.file = data.getFile();
    this.lines = data.getLines();
    this.commentLines = data.getCommentLines();
    this.commentRate = (double) this.commentLines / (double) this.lines;
  }

  public String getFile() {
    return this.file;
  }

  public long getLines() {
    return this.lines;
  }

  public long getCommentLines() {
    return this.commentLines;
  }

  public double getCommentRate() {
    return this.commentRate;
  }

  private static Comparator<FileCommentTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<FileCommentTableItem> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.comparing(FileCommentTableItem::getFile);
        break;
      case "lines":
        comparator = Comparator.comparing(FileCommentTableItem::getLines);
        break;
      case "commentLines":
        comparator = Comparator.comparing(FileCommentTableItem::getCommentLines);
        break;
      case "commentRate":
        comparator = Comparator.comparing(FileCommentTableItem::getCommentRate);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for comments table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for FileCommentTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<FileCommentTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<FileCommentTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}