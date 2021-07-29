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

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.Statistics;
import java.util.DoubleSummaryStatistics;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the StatementCoverageStatisticsCollector class.
 *
 * @author Sung Gon Kim
 */
public class StatementCoverageStatisticsCollectorTest {

  private StatisticsCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Statistics statistics;

  @Before
  public void setUp() {
    Configuration configuration = new Configuration();
    configuration.setStatementCoverageThreshold(50);
    collector = new StatementCoverageStatisticsCollector(configuration);
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    statistics = new Statistics(new DoubleSummaryStatistics(), 0.0);
  }

  private void assertValues(double min, double average, double max) {
    statistics = collector.parse(recipes);
    assertEquals(min, statistics.getMin(), 0.01);
    assertEquals(average, statistics.getAverage(), 0.01);
    assertEquals(max, statistics.getMax(), 0.01);
    assertEquals(0.5, statistics.getThreshold(), 0.01);
    assertEquals(0.0, statistics.getValue(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseRecipesWithNoTestData() {
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, true));
    assertValues(0.0, 0.0, 0.0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    assertValues(0.5, 0.5, 0.5);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new PassedTestData("A-A-A", "A", "X", "X"));
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));
    recipe1.add(new StatementCoverageData("A-A-A", "b.file", 1, false));
    recipe2.add(new PassedTestData("B-B-B", "B", "X", "X"));
    recipe2.add(new StatementCoverageData("B-B-B", "a.file", 1, true));
    recipe2.add(new StatementCoverageData("B-B-B", "b.file", 1, true));
    assertValues(0.5, 0.75, 1.0);
  }
}
