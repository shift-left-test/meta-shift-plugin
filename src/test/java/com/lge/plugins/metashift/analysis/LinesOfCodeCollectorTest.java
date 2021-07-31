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
  public void testParseSingRecipe() {
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
