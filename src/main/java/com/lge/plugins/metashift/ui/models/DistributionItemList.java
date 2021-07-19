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

import java.util.ArrayList;
import net.sf.json.JSONArray;

/**
 * recipe metric statistics item list class.
 */
public class DistributionItemList
    extends ArrayList<DistributionItemList.Item> {

  public void addItem(String label, String clazz, long width, long count) {
    this.add(new Item(label, clazz, width, count));
  }

  public JSONArray toJsonArray() {
    return JSONArray.fromObject(this);
  }

  /**
   * recipe statistics info.
   */
  public static class Item {

    private final String label;
    private final long width;
    private final long count;
    private final String clazz;

    /**
     * constructor.
     */
    public Item(String label, String clazz, long width, long count) {
      this.label = label;
      this.width = width;
      this.count = count;
      this.clazz = clazz;
    }

    public String getLabel() {
      return label;
    }

    public long getWidth() {
      return width;
    }

    public long getCount() {
      return count;
    }

    public String getClazz() {
      return clazz;
    }
  }
}