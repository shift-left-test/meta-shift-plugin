/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Statistics;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the StatisticsCollector class.
 *
 * @author Sung Gon Kim
 */
public class StatisticsCollectorTest {

  private Configuration configuration;
  private StatisticsCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    configuration = ConfigurationUtils.of(50, 5, false);
    collector = new StatisticsCollector(new PremirrorCacheEvaluator(configuration));
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(double min, double average, double max) {
    Statistics statistics = collector.parse(recipes);
    assertEquals(min, statistics.getMin(), 0.01);
    assertEquals(average, statistics.getAverage(), 0.01);
    assertEquals(max, statistics.getMax(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", true));
    assertValues(0.5, 0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PremirrorCacheData("A-A-A", "A", false));
    recipe1.add(new PremirrorCacheData("A-A-A", "B", true));
    recipe2.add(new PremirrorCacheData("B-B-B", "C", true));
    recipe2.add(new PremirrorCacheData("B-B-B", "D", true));
    assertValues(0.5, 0.75, 1.0);
  }

  @Test
  public void testParseWithAnotherEvaluator() {
    collector = new StatisticsCollector(new UnitTestEvaluator(configuration));
    recipe1.add(new FailedTestData("A-A-A", "A", "A", "A"));
    recipe2.add(new PassedTestData("B-B-B", "B", "B", "B"));
    assertValues(0.0, 0.5, 1.0);
  }
}
