/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the LinesOfCodeCollector class.
 *
 * @author Sung Gon Kim
 */
public class LinesOfCodeCollectorTest {

  private LinesOfCodeCollector collector;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private LinesOfCode linesOfCode;

  @Before
  public void setUp() {
    collector = new LinesOfCodeCollector();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    linesOfCode = new LinesOfCode(0, 0, 0, 0, 0);
  }

  private void assertValues(long lines, long functions, long classes, long files, long recipes) {
    linesOfCode = collector.parse(this.recipes);
    assertEquals(lines, linesOfCode.getLines());
    assertEquals(functions, linesOfCode.getFunctions());
    assertEquals(classes, linesOfCode.getClasses());
    assertEquals(files, linesOfCode.getFiles());
    assertEquals(recipes, linesOfCode.getRecipes());
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new BranchCoverageData("A-A-A", "a.file", 1, 1, true));
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testParseSingleRecipe() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 1, 1, 1));
    assertValues(2, 2, 2, 2, 1);
  }

  @Test
  public void testParseMultipleRecipes() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new CodeSizeData("A-A-A", "b.file", 1, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "a.file", 1, 1, 1));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 1, 1, 1));
    assertValues(4, 4, 4, 4, 2);
  }
}
