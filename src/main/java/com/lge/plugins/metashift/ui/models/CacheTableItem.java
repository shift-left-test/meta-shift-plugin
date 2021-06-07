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

import com.lge.plugins.metashift.models.CacheData;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Cache Table Item.
 */
public class CacheTableItem implements Serializable {
  private String signature;
  private boolean available;

  public CacheTableItem(CacheData cacheData) {
    this.signature = cacheData.getSignature();
    this.available = cacheData.isAvailable();
  }

  public String getSignature() {
    return this.signature;
  }

  public boolean isAvailable() {
    return this.available;
  }

  private static Comparator<CacheTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<CacheTableItem> comparator;

    switch (sortInfo.getField()) {
      case "signature":
        comparator = Comparator.comparing(CacheTableItem::getSignature);
        break;
      case "available":
        comparator = Comparator.<CacheTableItem, Boolean>comparing(CacheTableItem::isAvailable);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for premirror cache table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for CacheTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<CacheTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<CacheTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
