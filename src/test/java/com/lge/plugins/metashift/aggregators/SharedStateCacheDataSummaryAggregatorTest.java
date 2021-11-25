/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the SharedStateCacheDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class SharedStateCacheDataSummaryAggregatorTest {

  private static final String RECIPE1 = "A-1.0.0-r0";
  private static final String RECIPE2 = "B-1.0.0-r0";

  private SharedStateCacheDataSummaryAggregator aggregator;
  private List<DataSummary> summaries;
  private List<SharedStateCacheData> objects;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new SharedStateCacheDataSummaryAggregator(configuration);
    summaries = new ArrayList<>();
    objects = new ArrayList<>();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(String name, long linesOfCode, long hits, long misses, double ratio,
      boolean qualified) {
    summaries = aggregator.parse(recipes);
    DataSummary summary = summaries.stream()
        .filter(o -> o.getName().equals(name)).findFirst()
        .orElseThrow(AssertionError::new);
    assertEquals(linesOfCode, summary.getLinesOfCode());
    assertEquals(hits, summary.getFirst());
    assertEquals(misses, summary.getSecond());
    assertEquals(ratio, summary.getRatio(), 0.01);
    assertEquals(qualified, summary.isQualified());
  }

  private void assertValues(int index, String signature, boolean available) {
    objects = aggregator.parse(recipe1);
    assertEquals(signature, objects.get(index).getSignature());
    assertEquals(available, objects.get(index).isAvailable());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertEquals(0, (aggregator.parse(new Recipes())).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new SharedStateCacheData(RECIPE1, "A", false));
    assertValues(RECIPE1, 1, 0, 1, 0.0, false);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new SharedStateCacheData(RECIPE1, "A", false));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 2, 2, 2));
    recipe2.add(new SharedStateCacheData(RECIPE2, "B", true));
    recipe2.add(new SharedStateCacheData(RECIPE2, "C", true));
    assertValues(RECIPE1, 1, 0, 1, 0.0, false);
    assertValues(RECIPE2, 2, 2, 0, 1.0, true);
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new SharedStateCacheData(RECIPE1, "A", false));
    assertValues(0, "A", false);
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new SharedStateCacheData(RECIPE1, "A", false));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "C", true));
    assertValues(0, "A", false);
    assertValues(1, "B", true);
    assertValues(2, "C", true);
  }
}
