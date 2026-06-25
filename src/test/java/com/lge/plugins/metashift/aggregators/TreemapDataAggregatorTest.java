/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.analysis.RecipeEvaluator;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
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
    aggregator = new TreemapDataAggregator(
        new RecipeEvaluator(configuration),
        r -> r.objects(StatementCoverageData.class).count());
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
    // UnitTestEvaluator: available=true, qualified=true (1 passed / 1 total = 100% >= 50%)
    // RecipeEvaluator: denominator=1, numerator=1, ratio=1.0 → BEST
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, true));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
  }

  @Test
  public void testParseSingleRecipeWithMultipleMetrics() {
    // UnitTestEvaluator: available=true, qualified=true (1 passed / 1 total = 100% >= 50%)
    // StatementCoverageEvaluator: available=true (has TestData + StatementCoverageData), qualified=true
    // MutationTestEvaluator: available=true, qualified=true (1 killed / 1 total = 100% >= 5%)
    // RecipeEvaluator: denominator=3, numerator=3, ratio=1.0 → BEST
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, true));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe1.add(new KilledMutationTestData(RECIPE1, "a.file", "X", "X", 1, "X", "X"));
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
  }

  @Test
  public void testParseMultipleRecipesWithSingleMetric() {
    // recipe1: UnitTest available+qualified → ratio=1.0
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, true));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    // recipe2: UnitTest available+not-qualified (failed only) → ratio=0.0
    recipe2.add(new StatementCoverageData(RECIPE2, "b.file", 1, false));
    recipe2.add(new StatementCoverageData(RECIPE2, "b.file", 2, false));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    // max=1.0 (from recipe1). recipe1→BEST, recipe2→WORST
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
    assertValues(1, RECIPE2, 2, 0.0, Grade.WORST);
  }

  @Test
  public void testParseMultipleRecipesWithMultipleMetrics() {
    // recipe1: UnitTest available+qualified, StatementCoverage available+qualified → 2/2=1.0
    recipe1.add(new StatementCoverageData(RECIPE1, "a.file", 1, true));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    // recipe2: UnitTest available+not-qualified (0/1), StatementCoverage available+not-qualified → 0/2=0.0
    recipe2.add(new StatementCoverageData(RECIPE2, "b.file", 1, false));
    recipe2.add(new StatementCoverageData(RECIPE2, "b.file", 2, false));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    // max=1.0. recipe1→BEST, recipe2→WORST
    assertValues(0, RECIPE1, 1, 1.0, Grade.BEST);
    assertValues(1, RECIPE2, 2, 0.0, Grade.WORST);
  }

  @Test
  public void testSizeFunctionReflectedInLinesOfCode() {
    // 크기 함수가 박스 크기(getLinesOfCode)에 반영되는지 확인
    // StatementCoverageData 10개를 가진 recipe → getLinesOfCode() = 10
    for (int i = 0; i < 10; i++) {
      recipe1.add(new StatementCoverageData(RECIPE1, "a.file", i + 1, true));
    }
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    assertValues(0, RECIPE1, 10, 1.0, Grade.BEST);
  }
}
