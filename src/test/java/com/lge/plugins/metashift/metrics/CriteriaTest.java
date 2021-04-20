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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Criteria class.
 *
 * @author Sung Gon Kim
 */
public class CriteriaTest {

  private Criteria criteria;

  @Before
  public void setUp() {
    criteria = new Criteria();
  }

  private void assertValues(double overallThreshold,
      double cacheThreshold,
      double recipeViolationThreshold,
      double commentThreshold,
      double codeViolationThreshold,
      long complexityLevel,
      double complexityThreshold,
      double duplicationThreshold,
      double testThreshold,
      double coverageThreshold,
      double mutationTestThreshold) {
    assertEquals(overallThreshold, criteria.getOverallThreshold(), 0.1);
    assertEquals(cacheThreshold, criteria.getCacheThreshold(), 0.1);
    assertEquals(recipeViolationThreshold, criteria.getRecipeViolationThreshold(), 0.1);
    assertEquals(commentThreshold, criteria.getCommentThreshold(), 0.1);
    assertEquals(codeViolationThreshold, criteria.getCodeViolationThreshold(), 0.1);
    assertEquals(complexityLevel, criteria.getComplexityLevel());
    assertEquals(complexityThreshold, criteria.getComplexityThreshold(), 0.1);
    assertEquals(duplicationThreshold, criteria.getDuplicationThreshold(), 0.1);
    assertEquals(testThreshold, criteria.getTestThreshold(), 0.1);
    assertEquals(coverageThreshold, criteria.getCoverageThreshold(), 0.1);
    assertEquals(mutationTestThreshold, criteria.getMutationTestThreshold(), 0.1);
  }

  @Test
  public void testInitData() {
    assertValues(1.0, 0.8, 0.1, 0.3, 0.1, 4, 0.1, 0.1, 0.95, 0.6, 0.85);
  }

  @Test
  public void testSetData() {
    criteria.setOverallThreshold(0.1);
    criteria.setCacheThreshold(0.2);
    criteria.setRecipeViolationThreshold(0.3);
    criteria.setCommentThreshold(0.4);
    criteria.setCodeViolationThreshold(0.5);
    criteria.setComplexityLevel(6);
    criteria.setComplexityThreshold(0.6);
    criteria.setDuplicationThreshold(0.7);
    criteria.setTestThreshold(0.8);
    criteria.setCoverageThreshold(0.9);
    criteria.setMutationTestThreshold(1.0);
    assertValues(0.1, 0.2, 0.3, 0.4, 0.5, 6, 0.6, 0.7, 0.8, 0.9, 1.0);
  }
}
