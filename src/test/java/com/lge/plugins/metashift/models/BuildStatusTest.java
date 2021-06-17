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

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the BuildStatus interface.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusTest {

  private BuildStatus status;

  @Before
  public void setUp() {
    status = new Configuration();
  }

  private void assertValues(boolean premirrorCache, boolean sharedStateCache,
      boolean codeViolations, boolean comments, boolean complexity, boolean coverage,
      boolean duplications, boolean mutationTest, boolean recipeViolations, boolean test) {
    assertEquals(premirrorCache, status.isPremirrorCacheAsUnstable());
    assertEquals(sharedStateCache, status.isSharedStateCacheAsUnstable());
    assertEquals(codeViolations, status.isCodeViolationsAsUnstable());
    assertEquals(comments, status.isCommentsAsUnstable());
    assertEquals(complexity, status.isComplexityAsUnstable());
    assertEquals(coverage, status.isCoverageAsUnstable());
    assertEquals(duplications, status.isDuplicationsAsUnstable());
    assertEquals(mutationTest, status.isMutationTestAsUnstable());
    assertEquals(recipeViolations, status.isRecipeViolationsAsUnstable());
    assertEquals(test, status.isTestAsUnstable());
  }

  @Test
  public void testInitData() {
    assertValues(true, true, true, true, true, true, true, true, true, true);
  }

  @Test
  public void setTestData() {
    status.setPremirrorCacheAsUnstable(false);
    status.setSharedStateCacheAsUnstable(false);
    status.setCodeViolationsAsUnstable(false);
    status.setCommentsAsUnstable(false);
    status.setComplexityAsUnstable(false);
    status.setCoverageAsUnstable(false);
    status.setDuplicationsAsUnstable(false);
    status.setMutationTestAsUnstable(false);
    status.setRecipeViolationsAsUnstable(false);
    status.setTestAsUnstable(false);
    assertValues(false, false, false, false, false, false, false, false, false, false);
  }
}
