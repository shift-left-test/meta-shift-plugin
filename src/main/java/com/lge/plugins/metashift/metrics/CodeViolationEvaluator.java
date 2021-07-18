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

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CodeViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class CodeViolationEvaluator extends ViolationEvaluator<CodeViolationEvaluator> {

  /**
   * Represents the build status of the metric.
   */
  private final boolean buildStatus;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public CodeViolationEvaluator(final Configuration configuration) {
    super(configuration.getCodeViolationThreshold());
    buildStatus = configuration.isCodeViolationsAsUnstable();
  }

  @Override
  public boolean isStable() {
    return !buildStatus || !isAvailable() || isQualified();
  }

  @Override
  protected void parseImpl(final Streamable c) {
    setMajor(new Counter(
        c.objects(CodeViolationData.class).count(),
        c.objects(MajorCodeViolationData.class).count()
    ));
    setMinor(new Counter(
        c.objects(CodeViolationData.class).count(),
        c.objects(MinorCodeViolationData.class).count()
    ));
    setInfo(new Counter(
        c.objects(CodeViolationData.class).count(),
        c.objects(InfoCodeViolationData.class).count()
    ));

    setAvailable(c.isAvailable(CodeSizeData.class) && c.isAvailable(CodeViolationData.class));
    setDenominator(c.objects(CodeSizeData.class).mapToLong(CodeSizeData::getLines).sum());
    setNumerator(getMajor().getNumerator() + getMinor().getNumerator() + getInfo().getNumerator());
  }
}
