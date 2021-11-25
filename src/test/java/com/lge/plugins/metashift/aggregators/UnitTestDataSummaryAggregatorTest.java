/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the UnitTestDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class UnitTestDataSummaryAggregatorTest {

  private static final String RECIPE1 = "A-1.0.0-r0";
  private static final String RECIPE2 = "B-1.0.0-r0";

  private UnitTestDataSummaryAggregator aggregator;
  private List<DataSummary> summaries;
  private List<TestData> objects;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new UnitTestDataSummaryAggregator(configuration);
    summaries = new ArrayList<>();
    objects = new ArrayList<>();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(String name, long linesOfCode, long passed, long failed, long error,
      long skipped, double ratio, boolean qualified) {
    summaries = aggregator.parse(recipes);
    DataSummary summary = summaries.stream()
        .filter(o -> o.getName().equals(name)).findFirst()
        .orElseThrow(AssertionError::new);
    assertEquals(linesOfCode, summary.getLinesOfCode());
    assertEquals(passed, summary.getFirst());
    assertEquals(failed, summary.getSecond());
    assertEquals(error, summary.getThird());
    assertEquals(skipped, summary.getFourth());
    assertEquals(ratio, summary.getRatio(), 0.01);
    assertEquals(qualified, summary.isQualified());
  }

  private void assertValues(int index, String suite, String test, String message, String status) {
    objects = aggregator.parse(recipe1);
    assertEquals(suite, objects.get(index).getSuite());
    assertEquals(test, objects.get(index).getTest());
    assertEquals(message, objects.get(index).getMessage());
    assertEquals(status, objects.get(index).getStatus());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new FailedTestData(RECIPE1, "B", "B", "B"));
    assertValues(RECIPE1, 1, 1, 1, 0, 0, 0.5, true);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new FailedTestData(RECIPE1, "B", "B", "B"));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 2, 2, 2));
    recipe2.add(new ErrorTestData(RECIPE2, "C", "C", "C"));
    recipe2.add(new SkippedTestData(RECIPE2, "D", "D", "D"));
    assertValues(RECIPE1, 1, 1, 1, 0, 0, 0.5, true);
    assertValues(RECIPE2, 2, 0, 0, 1, 1, 0.0, false);
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new FailedTestData(RECIPE1, "B", "B", "B"));
    assertValues(0, "A", "A", "A", "PASSED");
    assertValues(1, "B", "B", "B", "FAILED");
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new FailedTestData(RECIPE1, "B", "B", "B"));
    recipe1.add(new ErrorTestData(RECIPE1, "C", "C", "C"));
    recipe1.add(new SkippedTestData(RECIPE1, "D", "D", "D"));
    assertValues(0, "A", "A", "A", "PASSED");
    assertValues(1, "B", "B", "B", "FAILED");
    assertValues(2, "C", "C", "C", "ERROR");
    assertValues(3, "D", "D", "D", "SKIPPED");
  }
}
