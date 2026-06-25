/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * EvaluationSummaryCollector class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummaryCollectorTest {

  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  private EvaluationSummaryCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private EvaluationSummary summary;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    collector = new EvaluationSummaryCollector(configuration);
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertUnitTests(EvaluationSummary o, boolean available, double ratio,
      boolean qualified) {
    assertEquals(available, o.getUnitTests().isAvailable());
    assertEquals(ratio, o.getUnitTests().getRatio(), 0.01);
    assertEquals(qualified, o.getUnitTests().isQualified());
  }

  private void assertStatementCoverage(EvaluationSummary o, boolean available, double ratio,
      boolean qualified) {
    assertEquals(available, o.getStatementCoverage().isAvailable());
    assertEquals(ratio, o.getStatementCoverage().getRatio(), 0.01);
    assertEquals(qualified, o.getStatementCoverage().isQualified());
  }

  @Test
  public void testParseEmptyRecipes() {
    summary = collector.parse(recipes);
    assertUnitTests(summary, false, 0.0, false);
    assertStatementCoverage(summary, false, 0.0, false);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, false));
    summary = collector.parse(recipes);
    assertUnitTests(summary, true, 1.0, true);
    assertStatementCoverage(summary, true, 0.0, false);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, false));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    recipe2.add(new StatementCoverageData(RECIPE2, "b.file", 1, true));
    summary = collector.parse(recipes);
    assertUnitTests(summary, true, 0.5, true);
    assertStatementCoverage(summary, true, 0.5, true);
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, false));
    summary = collector.parse(recipe1);
    assertUnitTests(summary, true, 1.0, true);
    assertStatementCoverage(summary, true, 0.0, false);
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, false));
    recipe1.add(new FailedTestData(RECIPE1, "B", "B", "B"));
    recipe1.add(new StatementCoverageData(RECIPE1, "b.file", 1, true));
    summary = collector.parse(recipe1);
    assertUnitTests(summary, true, 0.5, true);
    assertStatementCoverage(summary, true, 0.5, true);
  }
}
