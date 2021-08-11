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

  private void assertSize(Streamable s, long size) {
    assertEquals(size, calculator.parse(s).longValue());
  }

  @Test
  public void testParseRecipesWithIndependentLinesBelowTolerance() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 49, 50));
    assertSize(recipes, 0);
  }

  @Test
  public void testParseRecipesWithIndependentLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 20));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 30, 50));
    assertSize(recipes, 40);
  }

  @Test
  public void testParseRecipesWithOverlappedLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 30));
    recipe2.add(new DuplicationData(RECIPE2, FILE1, 50, 20, 50));
    assertSize(recipes, 60);
  }

  @Test
  public void testParseFileWithIndependentLinesBelowTolerance() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 49, 50));
    assertSize(recipe1, 0);
  }

  @Test
  public void testParseFileWithIndependentLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 20));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 30, 50));
    assertSize(recipe1, 40);
  }

  @Test
  public void testParseFileWithOverlappedLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 30));
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 20, 50));
    assertSize(recipe1, 50);
  }

  @Test
  public void testParseFilesWithIndependentLinesBelowTolerance() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 1));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 49, 50));
    assertSize(recipe1, 0);
  }

  @Test
  public void testParseFilesWithIndependentLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 20));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 30, 50));
    assertSize(recipe1, 40);
  }

  @Test
  public void testParseFilesWithOverlappedLines() {
    recipe1.add(new DuplicationData(RECIPE1, FILE1, 50, 0, 30));
    recipe1.add(new DuplicationData(RECIPE1, FILE2, 50, 20, 50));
    assertSize(recipe1, 60);
  }
}
