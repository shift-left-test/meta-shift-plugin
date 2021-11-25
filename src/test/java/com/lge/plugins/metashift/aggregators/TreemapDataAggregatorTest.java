/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.analysis.RecipeEvaluator;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.TreemapData;
import com.lge.plugins.metashift.models.TreemapData.Grade;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the TreemapDataAggregator class.
 *
 * @author Sung Gon Kim
 */
public class TreemapDataAggregatorTest {

  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  private TreemapDataAggregator aggregator;
  private List<TreemapData> objects;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new TreemapDataAggregator(new RecipeEvaluator(configuration));
    objects = new ArrayList<>();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(int index, String recipe, long linesOfCode, double value, Grade grade) {
    objects = aggregator.parse(recipes);
    assertEquals(recipe, objects.get(index).getName());
    assertEquals(linesOfCode, objects.get(index).getLinesOfCode());
    assertEquals(value, objects.get(index).getValue(), 0.01);
    assertEquals(grade.ordinal(), objects.get(index).getGrade());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipeWithSingleMetric() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
  }

  @Test
  public void testParseSingleRecipeWithMultipleMetrics() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    recipe1.add(new FailedTestData(RECIPE1, "A", "A", "A"));
    assertValues(0, RECIPE1, 1, 0.5, Grade.ORDINARY);
  }

  @Test
  public void testParseMultipleRecipesWithSingleMetric() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 2, 2, 2));
    recipe2.add(new PremirrorCacheData(RECIPE2, "B", false));
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
    assertValues(1, RECIPE2, 2, 0.0, Grade.WORST);
  }

  @Test
  public void testParseMultipleRecipesWithMultipleMetrics() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    recipe1.add(new FailedTestData(RECIPE1, "A", "A", "A"));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 2, 2, 2));
    recipe2.add(new PremirrorCacheData(RECIPE2, "B", false));
    recipe2.add(new PassedTestData(RECIPE2, "B", "B", "B"));
    assertValues(0, RECIPE1, 1, 0.5, Grade.ORDINARY);
    assertValues(1, RECIPE2, 2, 0.5, Grade.ORDINARY);
  }
}
