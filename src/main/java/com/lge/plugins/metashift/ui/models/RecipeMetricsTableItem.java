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

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import java.io.Serializable;
import java.util.Comparator;

/**
 * recipe metrics table item.
 */
public class RecipeMetricsTableItem implements Serializable {
  /**
   * metrics ratio and available.
   */
  public static class MetricsValue implements Serializable {
    private double ratio;
    private boolean available;

    /**
     * constructor.
     */
    public MetricsValue(Evaluator<?> evaluator) {
      if (evaluator != null) {
        this.available = evaluator.isAvailable();
        this.ratio = evaluator.getRatio();        
      } else {
        this.available = false;
        this.ratio = 0;
      }

    }

    public double getRatio() {
      return this.ratio;
    }

    public boolean isAvailable() {
      return this.available;
    }
  }

  private String name;
  private long lines;
  private MetricsValue premirrorCache;
  private MetricsValue sharedStateCache;
  private MetricsValue recipeViolations;
  private MetricsValue comments;
  private MetricsValue codeViolations;
  private MetricsValue complexity;
  private MetricsValue duplications;
  private MetricsValue test;
  private MetricsValue coverage;
  private MetricsValue mutationTest;

  /**
   * constructor.
   */
  public RecipeMetricsTableItem(String name, Metrics metrics) {
    this.name = name;
    this.lines = metrics.getCodeSize() != null ? metrics.getCodeSize().getLines() : 0;
    this.premirrorCache = new MetricsValue(metrics.getPremirrorCache());
    this.sharedStateCache = new MetricsValue(metrics.getSharedStateCache());
    this.recipeViolations = new MetricsValue(metrics.getRecipeViolations());
    this.comments = new MetricsValue(metrics.getComments());
    this.codeViolations = new MetricsValue(metrics.getCodeViolations());
    this.complexity = new MetricsValue(metrics.getComplexity());
    this.duplications = new MetricsValue(metrics.getDuplications());
    this.test = new MetricsValue(metrics.getTest());
    this.coverage = new MetricsValue(metrics.getCoverage());
    this.mutationTest = new MetricsValue(metrics.getMutationTest());
  }

  public String getName() {
    return this.name;
  }

  public long getLines() {
    return this.lines;
  }

  public MetricsValue getPremirrorCache() {
    return this.premirrorCache;
  }

  public MetricsValue getSharedStateCache() {
    return this.sharedStateCache;
  }

  public MetricsValue getRecipeViolations() {
    return this.recipeViolations;
  }

  public MetricsValue getComments() {
    return this.comments;
  }

  public MetricsValue getCodeViolations() {
    return this.codeViolations;
  }

  public MetricsValue getComplexity() {
    return this.complexity;
  }

  public MetricsValue getDuplications() {
    return this.duplications;
  }

  public MetricsValue getTest() {
    return this.test;
  }
  
  public MetricsValue getCoverage() {
    return this.coverage;
  }

  public MetricsValue getMutationTest() {
    return this.mutationTest;
  }

  private static Comparator<RecipeMetricsTableItem> createComparator(TableSortInfo sortInfo) {
    Comparator<RecipeMetricsTableItem> comparator;

    switch (sortInfo.getField()) {
      case "name":
        comparator = Comparator.comparing(RecipeMetricsTableItem::getName);
        break;
      case "lines":
        comparator = Comparator.comparing(RecipeMetricsTableItem::getLines);
        break;
      case "premirrorCache":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getPremirrorCache().getRatio());
        break;
      case "sharedStateCache":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getSharedStateCache().getRatio());
        break;
      case "recipeViolations":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getRecipeViolations().getRatio());
        break;
      case "comments":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getComments().getRatio());
        break;
      case "codeViolations":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getCodeViolations().getRatio());
        break;
      case "complexity":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getComplexity().getRatio());
        break;
      case "duplications":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getDuplications().getRatio());
        break;
      case "test":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getTest().getRatio());
        break;
      case "coverage":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getCoverage().getRatio());
        break;
      case "mutationTest":
        comparator = Comparator.<RecipeMetricsTableItem, Double>comparing(
            a -> a.getMutationTest().getRatio());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for recipe table : %s", sortInfo.getField()));
    }
    
    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return comparator for RecipeMetricsTableItem.
   *
   * @param sortInfos sort info
   * @return comparator
   */
  public static Comparator<RecipeMetricsTableItem> createComparator(TableSortInfo [] sortInfos) {
    Comparator<RecipeMetricsTableItem> comparator = createComparator(sortInfos[0]);

    for (int i = 1; i < sortInfos.length; i++) {
      comparator = comparator.thenComparing(createComparator(sortInfos[i]));
    }

    return comparator;
  }
}
