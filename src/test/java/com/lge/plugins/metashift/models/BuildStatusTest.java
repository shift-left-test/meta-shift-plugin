/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.utils.ConfigurationUtils;
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
      boolean codeViolations, boolean comments, boolean complexity, boolean statementCoverage,
      boolean branchCoverage, boolean duplications, boolean mutationTest, boolean recipeViolations,
      boolean test) {
    assertEquals(premirrorCache, status.isPremirrorCacheAsUnstable());
    assertEquals(sharedStateCache, status.isSharedStateCacheAsUnstable());
    assertEquals(codeViolations, status.isCodeViolationsAsUnstable());
    assertEquals(comments, status.isCommentsAsUnstable());
    assertEquals(complexity, status.isComplexityAsUnstable());
    assertEquals(statementCoverage, status.isStatementCoverageAsUnstable());
    assertEquals(branchCoverage, status.isBranchCoverageAsUnstable());
    assertEquals(duplications, status.isDuplicationsAsUnstable());
    assertEquals(mutationTest, status.isMutationTestAsUnstable());
    assertEquals(recipeViolations, status.isRecipeViolationsAsUnstable());
    assertEquals(test, status.isTestAsUnstable());
  }

  @Test
  public void testDefaultStatus() {
    assertValues(false, false, false, false, false, false, false, false, false, false, false);
  }

  @Test
  public void setBuildStatusAsFalse() {
    status = ConfigurationUtils.of(50, 5, false);
    assertValues(false, false, false, false, false, false, false, false, false, false, false);
  }

  @Test
  public void setBuildStatusAsTrue() {
    status = ConfigurationUtils.of(50, 5, true);
    assertValues(true, true, true, true, true, true, true, true, true, true, true);
  }
}
