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

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.BuildStatus;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.TestData;

/**
 * BranchCoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class BranchCoverageEvaluator extends PositiveEvaluator<BranchCoverageEvaluator> {

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public BranchCoverageEvaluator(final Criteria criteria) {
    super((double) criteria.getBranchCoverageThreshold() / 100.0);
  }

  @Override
  public boolean isStable(BuildStatus status) {
    return !status.isBranchCoverageAsUnstable() || !isAvailable() || isQualified();
  }

  @Override
  protected void parseImpl(final Streamable c) {
    setAvailable(c.isAvailable(TestData.class) && c.isAvailable(BranchCoverageData.class));
    setDenominator(c.objects(BranchCoverageData.class).count());
    setNumerator(c.objects(BranchCoverageData.class).filter(CoverageData::isCovered).count());
  }
}
