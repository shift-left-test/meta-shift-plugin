/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the EvaluationSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummaryAggregatorTest {

  private EvaluationSummaryAggregator aggregator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private List<EvaluationSummary> summaries;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new EvaluationSummaryAggregator(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    summaries = new ArrayList<>();
  }

  private void assertValues(int index, long linesOfCode, double premirrorCache,
      double sharedStateCache) {
    summaries = aggregator.parse(recipes);
    assertEquals(linesOfCode, summaries.get(index).getLinesOfCode());
    assertEquals(premirrorCache, summaries.get(index).getPremirrorCache().getRatio(), 0.01);
    assertEquals(sharedStateCache, summaries.get(index).getSharedStateCache().getRatio(), 0.01);
  }

  @Test
  public void testEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new SharedStateCacheData("A-A-A", "B", true));
    assertValues(0, 1, 0.0, 1.0);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new SharedStateCacheData("A-A-A", "B", true));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 2, 2, 2));
    recipe2.add(new PremirrorCacheData("B-B-B", "C", true));
    recipe2.add(new SharedStateCacheData("B-B-B", "D", false));
    assertValues(0, 1, 0.0, 1.0);
    assertValues(1, 2, 1.0, 0.0);
  }
}
