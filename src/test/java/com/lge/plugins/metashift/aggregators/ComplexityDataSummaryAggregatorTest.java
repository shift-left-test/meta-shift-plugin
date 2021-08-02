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

package com.lge.plugins.metashift.aggregators;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.ComplexityData;
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
 * Unit tests for the ComplexityDataSummaryAggregator class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataSummaryAggregatorTest {

  private static final String RECIPE1 = "A-1.0.0-r0";
  private static final String RECIPE2 = "B-1.0.0-r0";

  private ComplexityDataSummaryAggregator aggregator;
  private List<DataSummary> summaries;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    aggregator = new ComplexityDataSummaryAggregator(configuration);
    summaries = new ArrayList<>();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertValues(String name, long linesOfCode, long abnormal, long normal, double ratio,
      boolean qualified) {
    DataSummary summary = summaries.stream()
        .filter(o -> o.getName().equals(name)).findFirst()
        .orElseThrow(AssertionError::new);
    assertEquals(linesOfCode, summary.getLinesOfCode().getLines());
    assertEquals(abnormal, summary.getDistribution().getFirst().getCount());
    assertEquals(normal, summary.getDistribution().getSecond().getCount());
    assertEquals(ratio, summary.getEvaluation().getRatio(), 0.01);
    assertEquals(qualified, summary.getEvaluation().isQualified());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertEquals(0, aggregator.parse(new Recipes()).size());
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new ComplexityData(RECIPE1, "a.file", "func1", 1, 1, 1));
    summaries = aggregator.parse(recipes);
    assertValues(RECIPE1, 1, 0, 1, 0.0, true);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new ComplexityData(RECIPE1, "a.file", "func1", 1, 1, 1));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 10, 0, 0));
    recipe2.add(new ComplexityData(RECIPE2, "b.file", "func2", 1, 1, 100));
    summaries = aggregator.parse(recipes);
    assertValues(RECIPE1, 1, 0, 1, 0.0, true);
    assertValues(RECIPE2, 10, 1, 0, 1.0, false);
  }

  @Test
  public void testParseEmptyRecipe() {
    assertEquals(0, aggregator.parse(recipe1).size());
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new ComplexityData(RECIPE1, "a.file", "func1", 1, 1, 1));
    summaries = aggregator.parse(recipe1);
    assertValues("a.file", 1, 0, 1, 0.0, true);
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new ComplexityData(RECIPE1, "a.file", "func1", 1, 1, 1));
    recipe1.add(new CodeSizeData(RECIPE1, "b.file", 10, 0, 0));
    recipe1.add(new ComplexityData(RECIPE1, "b.file", "func2", 1, 1, 100));
    summaries = aggregator.parse(recipe1);
    assertValues("a.file", 1, 0, 1, 0.0, true);
    assertValues("b.file", 10, 1, 0, 1.0, false);
  }
}
