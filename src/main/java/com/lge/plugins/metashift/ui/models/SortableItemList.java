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

import com.lge.plugins.metashift.utils.ListUtils;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Sortableitem list class.
 */
public abstract class SortableItemList<T> implements Serializable {

  /**
   * column sort info.
   */
  public static class SortInfo {

    private final String dir;
    private final String field;

    @DataBoundConstructor
    public SortInfo(String dir, String field) {
      this.dir = dir;
      this.field = field;
    }

    public String getDir() {
      return dir;
    }

    public String getField() {
      return field;
    }
  }

  protected List<T> items;

  public SortableItemList(List<T> items) {
    this.items = items;
  }

  public List<T> getItems() {
    return items;
  }

  abstract Comparator<T> createComparator(SortInfo sortInfo);

  /**
   * return comparator for CacheTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public SortableItemList<T> sort(SortInfo[] sortInfos) {
    if (sortInfos.length > 0) {
      Comparator<T> comparator = this.createComparator(sortInfos[0]);
      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.createComparator(sortInfos[i]));
      }
      this.items.sort(comparator);
    }

    return this;
  }

  /**
   * return requested page.
   */
  public JSONObject getPage(int pageIndex, int pageSize) {
    List<List<T>> pagedDataList = ListUtils.partition(this.items, pageSize);
    if (pageIndex < 1) {
      pageIndex = 1;
    } else if (pageIndex > pagedDataList.size()) {
      pageIndex = pagedDataList.size();
    }

    JSONObject result = new JSONObject();
    if (pageIndex > 0) {
      result.put("data", pagedDataList.get(pageIndex - 1));
    } else {
      result.put("data", null);
    }
    result.put("last_page", pagedDataList.size());
    return result;
  }
}
