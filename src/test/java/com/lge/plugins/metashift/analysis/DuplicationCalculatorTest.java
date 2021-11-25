/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DuplicationCalculator class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationCalculatorTest {

  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";
  private static final String FILE1 = "a.file";
  private static final String FILE2 = "b.file";

  private DuplicationCalculator calculator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    calculator = new DuplicationCalculator(configuration);
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private void assertSize(Streamable s, long lines, long duplicateLines) {
    assertEquals(lines, calculator.parse(s).getLines());
    assertEquals(duplicateLines, calculator.parse(s).getDuplicateLines());
  }

  @Test
  public void testParseRecipesWithIndependentLinesBelowTolerance() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe2.add(new CodeSizeData(RECIPE2, FILE1, 50, 1, 1));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 49, 50));
    assertSize(recipes, 100, 0);
  }

  @Test
  public void testParseRecipesWithIndependentLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 20));
    recipe2.add(new CodeSizeData(RECIPE2, FILE1, 50, 1, 1));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 31, 50));
    assertSize(recipes, 100, 40);
  }

  @Test
  public void testParseRecipesWithOverlappedLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 30));
    recipe2.add(new CodeSizeData(RECIPE2, FILE1, 50, 1, 1));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 21, 50));
    assertSize(recipes, 100, 60);
  }

  @Test
  public void testParseFileWithIndependentLinesBelowTolerance() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 49, 50));
    assertSize(recipe1, 50, 0);
  }

  @Test
  public void testParseFileWithIndependentLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 20));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 31, 50));
    assertSize(recipe1, 50, 40);
  }

  @Test
  public void testParseFileWithOverlappedLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 30));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 21, 50));
    assertSize(recipe1, 50, 50);
  }

  @Test
  public void testParseFilesWithIndependentLinesBelowTolerance() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe1.add(new CodeSizeData(RECIPE1, FILE2, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 49, 50));
    assertSize(recipe1, 100, 0);
  }

  @Test
  public void testParseFilesWithIndependentLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 20));
    recipe1.add(new CodeSizeData(RECIPE1, FILE2, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 31, 50));
    assertSize(recipe1, 100, 40);
  }

  @Test
  public void testParseFilesWithOverlappedLines() {
    recipe1.add(new CodeSizeData(RECIPE1, FILE1, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 1, 30));
    recipe1.add(new CodeSizeData(RECIPE1, FILE2, 50, 1, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 21, 50));
    assertSize(recipe1, 100, 60);
  }
}
