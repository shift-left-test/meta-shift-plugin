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

    configuration.setTestThreshold(threshold);
    configuration.setStatementCoverageThreshold(threshold);
    configuration.setBranchCoverageThreshold(threshold);
    configuration.setMutationTestThreshold(threshold);

    configuration.setTestAsUnstable(status);
    configuration.setStatementCoverageAsUnstable(status);
    configuration.setBranchCoverageAsUnstable(status);
    configuration.setMutationTestAsUnstable(status);

    return configuration;
  }
}
