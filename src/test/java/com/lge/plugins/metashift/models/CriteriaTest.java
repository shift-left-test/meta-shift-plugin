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

  private void assertValues(int testThreshold,
      int statementCoverageThreshold,
      int branchCoverageThreshold,
      int mutationTestThreshold) {
    assertEquals(testThreshold, criteria.getTestThreshold());
    assertEquals(statementCoverageThreshold, criteria.getStatementCoverageThreshold());
    assertEquals(branchCoverageThreshold, criteria.getBranchCoverageThreshold());
    assertEquals(mutationTestThreshold, criteria.getMutationTestThreshold());
  }

  @Test
  public void testInitData() {
    assertValues(95, 80, 40, 85);
  }

  @Test
  public void testSetData() {
    criteria.setTestThreshold(80);
    criteria.setStatementCoverageThreshold(90);
    criteria.setBranchCoverageThreshold(100);
    criteria.setMutationTestThreshold(100);
    assertValues(80, 90, 100, 100);
  }

  @Test
  public void testSetValuesBelowLimits() {
    criteria = new Configuration(-10, -10, -10, -10, false, false, false, false);
    assertValues(0, 0, 0, 0);
  }

  @Test
  public void testSetValuesOverLimits() {
    criteria = new Configuration(200, 200, 200, 200, false, false, false, false);
    assertValues(100, 100, 100, 100);
  }
}
