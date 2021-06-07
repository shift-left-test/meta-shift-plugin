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
import java.util.List;
import net.sf.json.JSONObject;

/**
 * tabulator page utils.
 */
public class TabulatorUtils {

  /**
   * return requested page.
   */
  public static <T> JSONObject getPage(int pageIndex, int pageSize, List<T> dataList) {
    List<List<T>> pagedDataList = ListUtils.partition(dataList, pageSize);
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
