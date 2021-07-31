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

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.TestData;

/**
 * StatementCoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class StatementCoverageEvaluator extends PositiveEvaluator<StatementCoverageEvaluator> {

  /**
   * Represents the build status of the metric.
   */
  private final boolean buildStatus;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public StatementCoverageEvaluator(final Configuration configuration) {
    super((double) configuration.getStatementCoverageThreshold() / 100.0);
    buildStatus = configuration.isStatementCoverageAsUnstable();
  }

  @Override
  public boolean isStable() {
    return !buildStatus || !isAvailable() || isQualified();
  }

  @Override
  protected void parseImpl(final Streamable c) {
    setAvailable(c.contains(TestData.class) && c.contains(StatementCoverageData.class));
    setDenominator(c.objects(StatementCoverageData.class).count());
    setNumerator(c.objects(StatementCoverageData.class).filter(CoverageData::isCovered).count());
  }
}
