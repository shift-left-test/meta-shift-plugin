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

package com.lge.plugins.metashift.utils;

import com.lge.plugins.metashift.models.Configuration;

/**
 * ConfigurationUtils class for tests.
 *
 * @author Sung Gon Kim
 */
public class ConfigurationUtils {

  public static Configuration of(int threshold, long tolerance, boolean status) {
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
