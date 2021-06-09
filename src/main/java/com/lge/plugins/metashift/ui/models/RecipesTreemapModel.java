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
import java.util.List;

/**
 * recipe treemap chart model.
 */
public class RecipesTreemapModel {

  /**
   * treemap data item.
   */
  public static class TreemapData {

    private final int value;
    private final int quality;
    private final String name;
    private final String path;

    /**
     * constructor.
     */
    public TreemapData(String name, String path, int value, int quality) {
      this.name = name;
      this.path = path;
      this.value = value;
      this.quality = quality;
    }

    public int[] getValue() {
      return new int[]{value, quality};
    }

    public String getName() {
      return name;
    }

    public String getPath() {
      return path;
    }
  }

  private final List<TreemapData> series;

  /**
   * constructor.
   */
  public RecipesTreemapModel() {
    this.series = new ArrayList<>();

    // add quality boundary
    this.series.add(new TreemapData("", "", 0, 0));
    this.series.add(new TreemapData("", "", 0, 100));
  }

  public void add(String name, String path, int value, int quality) {
    series.add(new TreemapData(name, path, value, quality));
  }

  public List<TreemapData> getSeries() {
    return series;
  }
}