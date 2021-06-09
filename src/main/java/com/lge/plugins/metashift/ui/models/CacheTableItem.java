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
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Table Item.
 */
public class CacheTableItem implements Serializable {

  private static final long serialVersionUID = -396700679379276827L;
  private static final Map<String, Comparator<CacheTableItem>> comparators;
  private final String signature;
  private final boolean available;

  static {
    comparators = new HashMap<>();
    comparators.put("signature", Comparator.comparing(CacheTableItem::getSignature));
    comparators.put("available", Comparator.comparing(CacheTableItem::isAvailable));
  }

  public CacheTableItem(CacheData cacheData) {
    signature = cacheData.getSignature();
    available = cacheData.isAvailable();
  }

  public String getSignature() {
    return signature;
  }

  public boolean isAvailable() {
    return available;
  }

  private static Comparator<CacheTableItem> createComparator(TableSortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for premirror cache table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<CacheTableItem> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * return comparator for CacheTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<CacheTableItem> createComparator(TableSortInfo[] sortInfos) {
    Comparator<CacheTableItem> comparator = createComparator(sortInfos[0]);
    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }
    return comparator;
  }
}
