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
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
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

  private void assertLinesOfCode(EvaluationSummary o, long lines, long functions, long classes,
      long files, long recipes) {
    assertEquals(lines, o.getLinesOfCode().getLines());
    assertEquals(functions, o.getLinesOfCode().getFunctions());
    assertEquals(classes, o.getLinesOfCode().getClasses());
    assertEquals(files, o.getLinesOfCode().getFiles());
    assertEquals(recipes, o.getLinesOfCode().getRecipes());
  }

  private void assertPremirrorCache(EvaluationSummary o, boolean available, double ratio,
      boolean qualified) {
    assertEquals(available, o.getPremirrorCache().isAvailable());
    assertEquals(ratio, o.getPremirrorCache().getRatio(), 0.01);
    assertEquals(qualified, o.getPremirrorCache().isQualified());
  }

  private void assertSharedStateCache(EvaluationSummary o, boolean available, double ratio,
      boolean qualified) {
    assertEquals(available, o.getSharedStateCache().isAvailable());
    assertEquals(ratio, o.getSharedStateCache().getRatio(), 0.01);
    assertEquals(qualified, o.getSharedStateCache().isQualified());
  }

  @Test
  public void testParseEmptyRecipes() {
    summary = collector.parse(recipes);
    assertPremirrorCache(summary, false, 0.0, false);
    assertSharedStateCache(summary, false, 0.0, false);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", false));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", false));
    summary = collector.parse(recipes);
    assertPremirrorCache(summary, true, 0.0, false);
    assertSharedStateCache(summary, true, 0.0, false);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", false));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", false));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 2, 2, 2));
    recipe2.add(new PremirrorCacheData(RECIPE2, "C", true));
    recipe2.add(new SharedStateCacheData(RECIPE2, "D", true));
    summary = collector.parse(recipes);
    assertPremirrorCache(summary, true, 0.5, true);
    assertSharedStateCache(summary, true, 0.5, true);
  }

  @Test
  public void testParseRecipeWithSingleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", false));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", false));
    summary = collector.parse(recipe1);
    assertPremirrorCache(summary, true, 0.0, false);
    assertSharedStateCache(summary, true, 0.0, false);
  }

  @Test
  public void testParseRecipeWithMultipleData() {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 1, 1));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", false));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", false));
    recipe1.add(new CodeSizeData(RECIPE1, "b.file", 2, 2, 2));
    recipe1.add(new PremirrorCacheData(RECIPE1, "C", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "D", true));
    summary = collector.parse(recipe1);
    assertPremirrorCache(summary, true, 0.5, true);
    assertSharedStateCache(summary, true, 0.5, true);
  }
}
