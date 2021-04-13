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

package com.lge.plugins.metashift.metrics;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Recipe;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the CodeSizeDelta class.
 *
 * @author Sung Gon Kim
 */
public class CodeSizeDeltaTest {

  private Recipe recipe;
  private CodeSizeEvaluator first;
  private CodeSizeEvaluator second;
  private CodeSizeDelta delta;

  @Before
  public void setUp() {
    recipe = new Recipe("A-1.0.0-r0");
    first = new CodeSizeEvaluator();
    second = new CodeSizeEvaluator();
    delta = new CodeSizeDelta();
  }

  private void assertValues(long recipes, long files, long lines, long functions, long classes) {
    assertEquals(recipes, delta.getRecipes());
    assertEquals(files, delta.getFiles());
    assertEquals(lines, delta.getLines());
    assertEquals(functions, delta.getFunctions());
    assertEquals(classes, delta.getClasses());
  }

  @Test
  public void testInitialState() {
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testGetDeltaBetweenEmptyObjects() {
    delta = CodeSizeDelta.between(first, second);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testGetDeltaBetweenNullObjects() {
    delta = CodeSizeDelta.between(null, null);
    assertValues(0, 0, 0, 0, 0);
  }

  @Test
  public void testGetDeltaWithEmptySecond() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "B.file", 7, 5, 3));
    first.parse(recipe);
    delta = CodeSizeDelta.between(first, second);
    assertValues(-1, -1, -7, -5, -3);
  }

  @Test
  public void testGetDeltaWithNullSecond() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "B.file", 7, 5, 3));
    first.parse(recipe);
    delta = CodeSizeDelta.between(first, null);
    assertValues(-1, -1, -7, -5, -3);
  }

  @Test
  public void testGetDeltaWithEmptyFirst() {
    recipe.add(new CodeSizeData("B-1.0.0-r0", "a.file", 3, 2, 6));
    second.parse(recipe);
    delta = CodeSizeDelta.between(first, second);
    assertValues(1, 1, 3, 2, 6);
  }

  @Test
  public void testGetDeltaWithNullFirst() {
    recipe.add(new CodeSizeData("B-1.0.0-r0", "a.file", 3, 2, 6));
    second.parse(recipe);
    delta = CodeSizeDelta.between(null, second);
    assertValues(1, 1, 3, 2, 6);
  }

  @Test
  public void testGetDeltaWithTwoObjects() {
    recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new CodeSizeData("A-1.0.0-r0", "B.file", 7, 5, 3));
    first.parse(recipe);
    recipe = new Recipe("B-1.0.0-r0");
    recipe.add(new CodeSizeData("B-1.0.0-r0", "a.file", 3, 2, 6));
    second.parse(recipe);
    delta = CodeSizeDelta.between(first, second);
    assertValues(0, 0, -4, -3, 3);
  }
}
