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

import hudson.model.Result;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the BuildStatusSummary class.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusSummaryTest {

  private BuildStatusSummary summary;

  @Before
  public void setUp() {
    summary = new BuildStatusSummary();
  }

  @Test
  public void testInitialStatus() {
    assertEquals(Result.SUCCESS, summary.getPremirrorCache());
    assertEquals(Result.SUCCESS, summary.getSharedStateCache());
    assertEquals(Result.SUCCESS, summary.getRecipeViolations());
    assertEquals(Result.SUCCESS, summary.getComments());
    assertEquals(Result.SUCCESS, summary.getCodeViolations());
    assertEquals(Result.SUCCESS, summary.getComplexity());
    assertEquals(Result.SUCCESS, summary.getDuplications());
    assertEquals(Result.SUCCESS, summary.getUnitTests());
    assertEquals(Result.SUCCESS, summary.getStatementCoverage());
    assertEquals(Result.SUCCESS, summary.getBranchCoverage());
    assertEquals(Result.SUCCESS, summary.getMutationTests());
    assertEquals(Result.SUCCESS, summary.getCombined());
  }

  @Test
  public void testCreateObject() {
    summary = new BuildStatusSummary(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE,
        Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE,
        Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE);

    assertEquals(Result.UNSTABLE, summary.getPremirrorCache());
    assertEquals(Result.UNSTABLE, summary.getSharedStateCache());
    assertEquals(Result.UNSTABLE, summary.getRecipeViolations());
    assertEquals(Result.UNSTABLE, summary.getComments());
    assertEquals(Result.UNSTABLE, summary.getCodeViolations());
    assertEquals(Result.UNSTABLE, summary.getComplexity());
    assertEquals(Result.UNSTABLE, summary.getDuplications());
    assertEquals(Result.UNSTABLE, summary.getUnitTests());
    assertEquals(Result.UNSTABLE, summary.getStatementCoverage());
    assertEquals(Result.UNSTABLE, summary.getBranchCoverage());
    assertEquals(Result.UNSTABLE, summary.getMutationTests());
    assertEquals(Result.UNSTABLE, summary.getCombined());
  }

  @Test
  public void testCombined() {
    summary = new BuildStatusSummary(Result.SUCCESS, Result.SUCCESS, Result.SUCCESS, Result.SUCCESS,
        Result.SUCCESS, Result.SUCCESS, Result.SUCCESS, Result.SUCCESS, Result.SUCCESS,
        Result.FAILURE, Result.FAILURE);
    assertEquals(Result.FAILURE, summary.getCombined());
  }
}
