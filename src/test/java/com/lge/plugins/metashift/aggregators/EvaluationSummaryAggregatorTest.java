/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
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

  private void assertValues(int index, double unitTests, double statementCoverage) {
    summaries = aggregator.parse(recipes);
    assertEquals(unitTests, summaries.get(index).getUnitTests().getRatio(), 0.01);
    assertEquals(statementCoverage, summaries.get(index).getStatementCoverage().getRatio(), 0.01);
  }

  @Test
  public void testEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    assertValues(0, 1.0, 0.0);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    recipe2.add(new FailedTestData("B-B-B", "B", "B", "B"));
    recipe2.add(new StatementCoverageData("B-B-B", "b.file", 1, true));
    assertValues(0, 1.0, 0.0);
    assertValues(1, 0.0, 1.0);
  }
}
