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

  private void assertValues(float overallThreshold,
      float cacheThreshold,
      float recipeViolationThreshold,
      float commentThreshold,
      float codeViolationThreshold,
      int complexityLevel,
      float complexityThreshold,
      float duplicationThreshold,
      float testThreshold,
      float coverageThreshold,
      float mutationTestThreshold) {
    assertEquals(overallThreshold, criteria.getOverallThreshold(), 0.1f);
    assertEquals(cacheThreshold, criteria.getCacheThreshold(), 0.1f);
    assertEquals(recipeViolationThreshold, criteria.getRecipeViolationThreshold(), 0.1f);
    assertEquals(commentThreshold, criteria.getCommentThreshold(), 0.1f);
    assertEquals(codeViolationThreshold, criteria.getCodeViolationThreshold(), 0.1f);
    assertEquals(complexityLevel, criteria.getComplexityLevel());
    assertEquals(complexityThreshold, criteria.getComplexityThreshold(), 0.1f);
    assertEquals(duplicationThreshold, criteria.getDuplicationThreshold(), 0.1f);
    assertEquals(testThreshold, criteria.getTestThreshold(), 0.1f);
    assertEquals(coverageThreshold, criteria.getCoverageThreshold(), 0.1f);
    assertEquals(mutationTestThreshold, criteria.getMutationTestThreshold(), 0.1f);
  }

  @Test
  public void testInitData() {
    assertValues(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
  }

  @Test
  public void testSetData() {
    criteria.setOverallThreshold(0.1f);
    criteria.setCacheThreshold(0.2f);
    criteria.setRecipeViolationThreshold(0.3f);
    criteria.setCommentThreshold(0.4f);
    criteria.setCodeViolationThreshold(0.5f);
    criteria.setComplexityLevel(6);
    criteria.setComplexityThreshold(0.6f);
    criteria.setDuplicationThreshold(0.7f);
    criteria.setTestThreshold(0.8f);
    criteria.setCoverageThreshold(0.9f);
    criteria.setMutationTestThreshold(1.0f);
    assertValues(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 6, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f);
  }
}
