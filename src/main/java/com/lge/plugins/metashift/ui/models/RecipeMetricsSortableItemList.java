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
import com.lge.plugins.metashift.metrics.Queryable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Recipe metrics sortableitem list class.
 */
public class RecipeMetricsSortableItemList
    extends SortableItemList<RecipeMetricsSortableItemList.Item> {

  private static final long serialVersionUID = 1L;
  private static final Map<String, Comparator<Item>> comparators;

  static {
    comparators = new HashMap<>();
    comparators.put("name", Comparator.comparing(Item::getName));
    comparators.put("lines", Comparator.comparing(Item::getLines));
    comparators.put("premirrorCache",
        Comparator.comparing(o -> o.getPremirrorCache().getRatio()));
    comparators.put("sharedStateCache",
        Comparator.comparing(o -> o.getSharedStateCache().getRatio()));
    comparators.put("recipeViolations",
        Comparator.comparing(o -> o.getRecipeViolations().getRatio()));
    comparators.put("comments", Comparator.comparing(o -> o.getComments().getRatio()));
    comparators.put("codeViolations", Comparator.comparing(o -> o.getCodeViolations().getRatio()));
    comparators.put("complexity", Comparator.comparing(o -> o.getComplexity().getRatio()));
    comparators.put("duplications", Comparator.comparing(o -> o.getDuplications().getRatio()));
    comparators.put("test", Comparator.comparing(o -> o.getTest().getRatio()));
    comparators.put("coverage", Comparator.comparing(o -> o.getCoverage().getRatio()));
    comparators.put("mutationTest", Comparator.comparing(o -> o.getMutationTest().getRatio()));
  }

  public RecipeMetricsSortableItemList() {
    super(new ArrayList<>());
  }

  public void addItem(String name, long lines, Queryable<Evaluator<?>> queryable) {
    this.items.add(new Item(name, lines, queryable));
  }

  protected Comparator<Item> createComparator(SortInfo sortInfo) {
    String field = sortInfo.getField();
    if (!comparators.containsKey(field)) {
      String message = String.format("unknown field for recipe table : %s", field);
      throw new IllegalArgumentException(message);
    }
    Comparator<Item> comparator = comparators.get(field);
    return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
  }

  /**
   * recipe metrics table item.
   */
  public static class Item implements Serializable {

    /**
     * metrics ratio and available.
     */
    public static class MetricsValue implements Serializable {

      private static final long serialVersionUID = 8635328617528636767L;
      private final double ratio;
      private final boolean available;

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
        return ratio;
      }

      public boolean isAvailable() {
        return available;
      }
    }

    private static final long serialVersionUID = 4673440225191529982L;
    private final String name;
    private final long lines;
    private final MetricsValue premirrorCache;
    private final MetricsValue sharedStateCache;
    private final MetricsValue recipeViolations;
    private final MetricsValue comments;
    private final MetricsValue codeViolations;
    private final MetricsValue complexity;
    private final MetricsValue duplications;
    private final MetricsValue test;
    private final MetricsValue coverage;
    private final MetricsValue mutationTest;

    /**
     * constructor.
     */
    public Item(String name, long lines, Queryable<Evaluator<?>> queryable) {
      this.name = name;
      this.lines = lines;
      this.premirrorCache = new MetricsValue(queryable.getPremirrorCache());
      this.sharedStateCache = new MetricsValue(queryable.getSharedStateCache());
      this.recipeViolations = new MetricsValue(queryable.getRecipeViolations());
      this.comments = new MetricsValue(queryable.getComments());
      this.codeViolations = new MetricsValue(queryable.getCodeViolations());
      this.complexity = new MetricsValue(queryable.getComplexity());
      this.duplications = new MetricsValue(queryable.getDuplications());
      this.test = new MetricsValue(queryable.getTest());
      this.coverage = new MetricsValue(queryable.getCoverage());
      this.mutationTest = new MetricsValue(queryable.getMutationTest());
    }

    public String getName() {
      return name;
    }

    public long getLines() {
      return lines;
    }

    public MetricsValue getPremirrorCache() {
      return premirrorCache;
    }

    public MetricsValue getSharedStateCache() {
      return sharedStateCache;
    }

    public MetricsValue getRecipeViolations() {
      return recipeViolations;
    }

    public MetricsValue getComments() {
      return comments;
    }

    public MetricsValue getCodeViolations() {
      return codeViolations;
    }

    public MetricsValue getComplexity() {
      return complexity;
    }

    public MetricsValue getDuplications() {
      return duplications;
    }

    public MetricsValue getTest() {
      return test;
    }

    public MetricsValue getCoverage() {
      return coverage;
    }

    public MetricsValue getMutationTest() {
      return mutationTest;
    }
  }
}
