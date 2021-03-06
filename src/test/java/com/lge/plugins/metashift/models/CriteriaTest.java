/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Criteria interface.
 *
 * @author Sung Gon Kim
 */
public class CriteriaTest {

  private Criteria criteria;

  @Before
  public void setUp() {
    criteria = new Configuration();
  }

  private void assertValues(int premirrorThreshold,
      int sharedStateCacheThreshold,
      double recipeViolationThreshold,
      int commentThreshold,
      double codeViolationThreshold,
      long complexityTolerance,
      int complexityThreshold,
      long duplicationTolerance,
      int duplicationThreshold,
      int testThreshold,
      int statementCoverageThreshold,
      int branchCoverageThreshold,
      int mutationTestThreshold) {
    assertEquals(premirrorThreshold, criteria.getPremirrorCacheThreshold());
    assertEquals(sharedStateCacheThreshold, criteria.getSharedStateCacheThreshold());
    assertEquals(recipeViolationThreshold, criteria.getRecipeViolationThreshold(), 0.1);
    assertEquals(commentThreshold, criteria.getCommentThreshold());
    assertEquals(codeViolationThreshold, criteria.getCodeViolationThreshold(), 0.1);
    assertEquals(complexityTolerance, criteria.getComplexityTolerance());
    assertEquals(complexityThreshold, criteria.getComplexityThreshold());
    assertEquals(duplicationTolerance, criteria.getDuplicationTolerance());
    assertEquals(duplicationThreshold, criteria.getDuplicationThreshold());
    assertEquals(testThreshold, criteria.getTestThreshold());
    assertEquals(statementCoverageThreshold, criteria.getStatementCoverageThreshold());
    assertEquals(branchCoverageThreshold, criteria.getBranchCoverageThreshold());
    assertEquals(mutationTestThreshold, criteria.getMutationTestThreshold());
  }

  @Test
  public void testInitData() {
    assertValues(80, 80, 0.1, 20, 0.1, 10, 10, 10, 10, 95, 80, 40, 85);
  }

  @Test
  public void testSetData() {
    criteria.setPremirrorCacheThreshold(10);
    criteria.setSharedStateCacheThreshold(20);
    criteria.setRecipeViolationThreshold(30);
    criteria.setCommentThreshold(40);
    criteria.setCodeViolationThreshold(50);
    criteria.setComplexityTolerance(6);
    criteria.setComplexityThreshold(60);
    criteria.setDuplicationTolerance(7);
    criteria.setDuplicationThreshold(70);
    criteria.setTestThreshold(80);
    criteria.setStatementCoverageThreshold(90);
    criteria.setBranchCoverageThreshold(100);
    criteria.setMutationTestThreshold(100);
    assertValues(10, 20, 30, 40, 50, 6, 60, 7, 70, 80, 90, 100, 100);
  }

  @Test
  public void testSetValuesBelowLimits() {
    criteria = new Configuration(-10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10,
        false, false, false, false, false, false, false, false, false, false, false);
    assertValues(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  @Test
  public void testSetValuesOverLimits() {
    criteria = new Configuration(200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200,
        false, false, false, false, false, false, false, false, false, false, false);
    assertValues(100, 100, 200, 100, 200, 200, 100, 200, 100, 100, 100, 100, 100);
  }
}
