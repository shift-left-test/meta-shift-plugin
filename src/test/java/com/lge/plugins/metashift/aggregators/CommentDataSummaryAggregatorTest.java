/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DataSummary;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CommentDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class CommentDataSummaryAggregatorTest {

  private static final String RECIPE1 = "A-1.0.0-r0";
  private static final String RECIPE2 = "B-1.0.0-r0";

  private CommentDataSummaryAggregator aggregator;
  private List<DataSummary> summaries;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new CommentDataSummaryAggregator(configuration);
    summaries = new ArrayList<>();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(String name, long linesOfCode, long comments, long others, double ratio,
      boolean qualified) {
    DataSummary summary = summaries.stream()
        .filter(o -> o.getName().equals(name)).findFirst()
        .orElseThrow(AssertionError::new);
    assertEquals(linesOfCode, summary.getLinesOfCode());
    assertEquals(comments, summary.getFirst());
    assertEquals(others, summary.getSecond());
    assertEquals(ratio, summary.getRatio(), 0.01);
    assertEquals(qualified, summary.isQualified());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new CommentData(RECIPE1, "a.file", 1, 0));
    summaries = aggregator.parse(recipes);
    assertValues(RECIPE1, 1, 0, 1, 0.0, false);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new CommentData(RECIPE1, "a.file", 1, 0));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 1, 0, 0));
    recipe2.add(new CommentData(RECIPE2, "b.file", 1, 1));
    summaries = aggregator.parse(recipes);
    assertValues(RECIPE1, 1, 0, 1, 0.0, false);
    assertValues(RECIPE2, 1, 1, 0, 1.0, true);
  }

  @Test
  public void testParseEmptyRecipe() {
    assertEquals(0, aggregator.parse(recipe1).size());
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new CommentData(RECIPE1, "a.file", 1, 0));
    summaries = aggregator.parse(recipe1);
    assertValues("a.file", 1, 0, 1, 0.0, false);
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new CommentData(RECIPE1, "a.file", 1, 0));
    recipe1.add(new CodeSizeData(RECIPE1, "b.file", 1, 0, 0));
    recipe1.add(new CommentData(RECIPE1, "b.file", 1, 1));
    summaries = aggregator.parse(recipe1);
    assertValues("a.file", 1, 0, 1, 0.0, false);
    assertValues("b.file", 1, 1, 0, 1.0, true);
  }
}
