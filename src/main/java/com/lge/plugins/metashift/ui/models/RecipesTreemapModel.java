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

import com.lge.plugins.metashift.metrics.Metrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

/**
 * recipe treemap chart model.
 */
public class RecipesTreemapModel {

  /**
   * treemap data item.
   */
  public static class TreemapData {

    private final long value;
    private final long quality;
    private final String name;
    private final String link;
    private final Map<String, Boolean> qualifiedMap;
  
    /**
     * constructor.
     */
    public TreemapData(String name, Metrics metrics) {
      this.name = name;
      this.link = name;

      this.qualifiedMap = new HashMap<>();

      if (metrics != null) {
        this.value = metrics.getCodeSize().getLines();
        this.quality = (long) (metrics.getRatio() * 100);
        
        if (metrics.getPremirrorCache().isAvailable()) {
          this.qualifiedMap.put("Premirror Cache",
              metrics.getPremirrorCache().isQualified());
        }
        if (metrics.getSharedStateCache().isAvailable()) {
          this.qualifiedMap.put("Shared State Cache",
              metrics.getSharedStateCache().isQualified());
        }
        if (metrics.getRecipeViolations().isAvailable()) {
          this.qualifiedMap.put("Recipe Violations",
              metrics.getRecipeViolations().isQualified());
        }
        if (metrics.getComments().isAvailable()) {
          this.qualifiedMap.put("Comments",
              metrics.getComments().isQualified());
        }
        if (metrics.getCodeViolations().isAvailable()) {
          this.qualifiedMap.put("Code Violations",
              metrics.getCodeViolations().isQualified());
        }
        if (metrics.getComplexity().isAvailable()) {
          this.qualifiedMap.put("Complexity",
              metrics.getComplexity().isQualified());
        }
        if (metrics.getDuplications().isAvailable()) {
          this.qualifiedMap.put("Duplications",
              metrics.getDuplications().isQualified());
        }
        if (metrics.getTest().isAvailable()) {
          this.qualifiedMap.put("Unit Tests",
              metrics.getTest().isQualified());
        }
        if (metrics.getStatementCoverage().isAvailable()) {
          this.qualifiedMap.put("Statement Coverage",
              metrics.getStatementCoverage().isQualified());
        }
        if (metrics.getBranchCoverage().isAvailable()) {
          this.qualifiedMap.put("Branch Coverage",
              metrics.getBranchCoverage().isQualified());
        }
        if (metrics.getMutationTest().isAvailable()) {
          this.qualifiedMap.put("Mutation Tests",
              metrics.getMutationTest().isQualified());
        }  
      } else {
        this.value = 0;
        this.quality = 0;
      }
    }

    /**
     * constructor.
     */
    public TreemapData(long value, long quality) {
      this.name = null;
      this.link = null;
      this.value = value;
      this.quality = quality;
      this.qualifiedMap = new HashMap<>();
    }

    public long[] getValue() {
      return new long[]{value, quality};
    }

    public String getName() {
      return name;
    }

    public String getLink() {
      return link;
    }

    // echart api needs path property.
    public String getPath() {
      return "";
    }

    // echart link option for node click target
    public String getTarget() {
      return "_self";
    }

    public Map<String, Boolean> getQualifiedMap() {
      return qualifiedMap;
    }
  }

  private final List<TreemapData> series;

  /**
   * constructor.
   */
  public RecipesTreemapModel() {
    this.series = new ArrayList<>();

    // add quality boundary
    this.series.add(new TreemapData(0, 0));
    this.series.add(new TreemapData(0, 100));
  }

  public void add(String name, Metrics metrics) {
    series.add(new TreemapData(name, metrics));
  }

  public List<TreemapData> getSeries() {
    return series;
  }

  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}