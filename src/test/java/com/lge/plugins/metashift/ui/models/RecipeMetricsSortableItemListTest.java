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

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.metrics.Queryable;
import com.lge.plugins.metashift.models.BuildStatus;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Streamable;
import org.junit.Test;

public class RecipeMetricsSortableItemListTest {

  public static class MockEvaluator extends Evaluator<MockEvaluator> {

    double ratio;

    public MockEvaluator(double ratio) {
      super(0);
      this.ratio = ratio;
    }

    @Override
    public boolean isQualified() {
      return true;
    }

    @Override
    public boolean isStable(BuildStatus status) {
      return true;
    }

    @Override
    public void parseImpl(final Streamable c) {
      // do nothing...
    }

    @Override
    public boolean isAvailable() {
      return true;
    }

    @Override
    public double getRatio() {
      return ratio;
    }
  }

  public static class MockQueryable implements Queryable<Evaluator<?>> {

    MockEvaluator premirrorCache;
    MockEvaluator sharedStateCache;
    MockEvaluator codeViolations;
    MockEvaluator comments;
    MockEvaluator complexity;
    MockEvaluator coverage;
    MockEvaluator duplications;
    MockEvaluator mutationTest;
    MockEvaluator recipeViolations;
    MockEvaluator test;

    public MockQueryable(
        double premirrorCacheRatio,
        double sharedStateCacheRatio,
        double codeViolationsRatio,
        double commentsRatio,
        double complexityRatio,
        double coverageRatio,
        double duplicationsRatio,
        double mutationTestRatio,
        double recipeViolationsRatio,
        double testRatio
    ) {
      this.premirrorCache = new MockEvaluator(premirrorCacheRatio);
      this.sharedStateCache = new MockEvaluator(sharedStateCacheRatio);
      this.codeViolations = new MockEvaluator(codeViolationsRatio);
      this.comments = new MockEvaluator(commentsRatio);
      this.complexity = new MockEvaluator(complexityRatio);
      this.coverage = new MockEvaluator(coverageRatio);
      this.duplications = new MockEvaluator(duplicationsRatio);
      this.mutationTest = new MockEvaluator(mutationTestRatio);
      this.recipeViolations = new MockEvaluator(recipeViolationsRatio);
      this.test = new MockEvaluator(testRatio);
    }

    public Evaluator<?> getPremirrorCache() {
      return premirrorCache;
    }

    public Evaluator<?> getSharedStateCache() {
      return sharedStateCache;
    }

    public Evaluator<?> getCodeViolations() {
      return codeViolations;
    }

    public Evaluator<?> getComments() {
      return comments;
    }

    public Evaluator<?> getComplexity() {
      return complexity;
    }

    public Evaluator<?> getCoverage() {
      return coverage;
    }

    public Evaluator<?> getDuplications() {
      return duplications;
    }

    public Evaluator<?> getMutationTest() {
      return mutationTest;
    }

    public Evaluator<?> getRecipeViolations() {
      return recipeViolations;
    }

    public Evaluator<?> getTest() {
      return test;
    }
  }

  @Test
  public void testAddItem() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    assertEquals(0, itemList.getItems().size());

    Configuration config = new Configuration();
    itemList.addItem("test-0-0", 10, new Metrics(config));

    assertEquals(1, itemList.getItems().size());
    assertEquals("test-0-0", itemList.getItems().get(0).getName());
  }

  @Test
  public void testSortByNameAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "name")
    });

    assertEquals("test1-0-0", itemList.getItems().get(0).getName());
    assertEquals("test2-0-0", itemList.getItems().get(1).getName());
    assertEquals("test3-0-0", itemList.getItems().get(2).getName());
  }

  @Test
  public void testSortByNameDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "name")
    });

    assertEquals("test3-0-0", itemList.getItems().get(0).getName());
    assertEquals("test2-0-0", itemList.getItems().get(1).getName());
    assertEquals("test1-0-0", itemList.getItems().get(2).getName());
  }

  @Test
  public void testSortByLinesAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "lines")
    });

    assertEquals(10, itemList.getItems().get(0).getLines());
    assertEquals(20, itemList.getItems().get(1).getLines());
    assertEquals(30, itemList.getItems().get(2).getLines());
  }

  @Test
  public void testSortByLinesDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "lines")
    });

    assertEquals(30, itemList.getItems().get(0).getLines());
    assertEquals(20, itemList.getItems().get(1).getLines());
    assertEquals(10, itemList.getItems().get(2).getLines());
  }

  @Test
  public void testSortByPremirrorCacheAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "premirrorCache")
    });

    assertEquals(0.1, itemList.getItems().get(0).getPremirrorCache().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getPremirrorCache().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getPremirrorCache().getRatio(), 0);
  }

  @Test
  public void testSortByPremirrorCacheDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "premirrorCache")
    });

    assertEquals(0.3, itemList.getItems().get(0).getPremirrorCache().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getPremirrorCache().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getPremirrorCache().getRatio(), 0);
  }

  @Test
  public void testSortBySharedStateCacheAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "sharedStateCache")
    });

    assertEquals(0.1, itemList.getItems().get(0).getSharedStateCache().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getSharedStateCache().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getSharedStateCache().getRatio(), 0);
  }

  @Test
  public void testSortBySharedStateCacheDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "sharedStateCache")
    });

    assertEquals(0.3, itemList.getItems().get(0).getSharedStateCache().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getSharedStateCache().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getSharedStateCache().getRatio(), 0);
  }

  @Test
  public void testSortByRecipeViolationsAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "recipeViolations")
    });

    assertEquals(0.1, itemList.getItems().get(0).getRecipeViolations().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getRecipeViolations().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getRecipeViolations().getRatio(), 0);
  }

  @Test
  public void testSortByRecipeViolationsDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "recipeViolations")
    });

    assertEquals(0.3, itemList.getItems().get(0).getRecipeViolations().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getRecipeViolations().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getRecipeViolations().getRatio(), 0);
  }

  @Test
  public void testSortByCommentsAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "comments")
    });

    assertEquals(0.1, itemList.getItems().get(0).getComments().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getComments().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getComments().getRatio(), 0);
  }

  @Test
  public void testSortByCommentsDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "comments")
    });

    assertEquals(0.3, itemList.getItems().get(0).getComments().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getComments().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getComments().getRatio(), 0);
  }

  @Test
  public void testSortByCodeViolationsAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "codeViolations")
    });

    assertEquals(0.1, itemList.getItems().get(0).getCodeViolations().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getCodeViolations().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getCodeViolations().getRatio(), 0);
  }

  @Test
  public void testSortByCodeViolationsDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "codeViolations")
    });

    assertEquals(0.3, itemList.getItems().get(0).getCodeViolations().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getCodeViolations().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getCodeViolations().getRatio(), 0);
  }

  @Test
  public void testSortByComplexityAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "complexity")
    });

    assertEquals(0.1, itemList.getItems().get(0).getComplexity().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getComplexity().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getComplexity().getRatio(), 0);
  }

  @Test
  public void testSortByComplexityDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "complexity")
    });

    assertEquals(0.3, itemList.getItems().get(0).getComplexity().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getComplexity().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getComplexity().getRatio(), 0);
  }

  @Test
  public void testSortByDuplicationsAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "duplications")
    });

    assertEquals(0.1, itemList.getItems().get(0).getDuplications().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getDuplications().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getDuplications().getRatio(), 0);
  }

  @Test
  public void testSortByDuplicationsDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "duplications")
    });

    assertEquals(0.3, itemList.getItems().get(0).getDuplications().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getDuplications().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getDuplications().getRatio(), 0);
  }

  @Test
  public void testSortByTestAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "test")
    });

    assertEquals(0.1, itemList.getItems().get(0).getTest().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getTest().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getTest().getRatio(), 0);
  }

  @Test
  public void testSortByTestDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "test")
    });

    assertEquals(0.3, itemList.getItems().get(0).getTest().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getTest().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getTest().getRatio(), 0);
  }

  @Test
  public void testSortByCoverageAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "coverage")
    });

    assertEquals(0.1, itemList.getItems().get(0).getCoverage().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getCoverage().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getCoverage().getRatio(), 0);
  }

  @Test
  public void testSortByCoverageDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "coverage")
    });

    assertEquals(0.3, itemList.getItems().get(0).getCoverage().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getCoverage().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getCoverage().getRatio(), 0);
  }

  @Test
  public void testSortByMutationTestAsc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "mutationTest")
    });

    assertEquals(0.1, itemList.getItems().get(0).getMutationTest().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getMutationTest().getRatio(), 0);
    assertEquals(0.3, itemList.getItems().get(2).getMutationTest().getRatio(), 0);
  }

  @Test
  public void testSortByMutationTestDesc() {
    RecipeMetricsSortableItemList itemList = new RecipeMetricsSortableItemList();

    itemList.addItem("test3-0-0", 30,
        new MockQueryable(0.1, 0.2, 0.1, 0.3, 0.2, 0.2, 0.1, 0.1, 0.3, 0.2));
    itemList.addItem("test1-0-0", 10,
        new MockQueryable(0.3, 0.1, 0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.1, 0.1));
    itemList.addItem("test2-0-0", 20,
        new MockQueryable(0.2, 0.3, 0.3, 0.1, 0.1, 0.3, 0.2, 0.3, 0.2, 0.3));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "mutationTest")
    });

    assertEquals(0.3, itemList.getItems().get(0).getMutationTest().getRatio(), 0);
    assertEquals(0.2, itemList.getItems().get(1).getMutationTest().getRatio(), 0);
    assertEquals(0.1, itemList.getItems().get(2).getMutationTest().getRatio(), 0);
  }
}
