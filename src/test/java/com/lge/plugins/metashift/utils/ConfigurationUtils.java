/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import com.lge.plugins.metashift.models.Configuration;

/**
 * ConfigurationUtils class for tests.
 *
 * @author Sung Gon Kim
 */
public class ConfigurationUtils {

  public static Configuration of(int threshold, int tolerance, boolean status) {
    Configuration configuration = new Configuration();

    configuration.setPremirrorCacheThreshold(threshold);
    configuration.setSharedStateCacheThreshold(threshold);
    configuration.setRecipeViolationThreshold((double) threshold / 100.0);
    configuration.setCommentThreshold(threshold);
    configuration.setCodeViolationThreshold((double) threshold / 100.0);
    configuration.setComplexityTolerance(tolerance);
    configuration.setComplexityThreshold(threshold);
    configuration.setDuplicationTolerance(tolerance);
    configuration.setDuplicationThreshold(threshold);
    configuration.setTestThreshold(threshold);
    configuration.setStatementCoverageThreshold(threshold);
    configuration.setBranchCoverageThreshold(threshold);
    configuration.setMutationTestThreshold(threshold);

    configuration.setPremirrorCacheAsUnstable(status);
    configuration.setSharedStateCacheAsUnstable(status);
    configuration.setRecipeViolationsAsUnstable(status);
    configuration.setCommentsAsUnstable(status);
    configuration.setCodeViolationsAsUnstable(status);
    configuration.setComplexityAsUnstable(status);
    configuration.setDuplicationsAsUnstable(status);
    configuration.setTestAsUnstable(status);
    configuration.setStatementCoverageAsUnstable(status);
    configuration.setBranchCoverageAsUnstable(status);
    configuration.setMutationTestAsUnstable(status);

    return configuration;
  }
}
