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

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * MutationTestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestEvaluator implements Evaluator {

  private final Configuration configuration;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public MutationTestEvaluator(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Evaluation parse(Streamable s) {
    boolean available = s.contains(MutationTestData.class);
    long denominator = s.objects(MutationTestData.class).count();
    long numerator = s.objects(KilledMutationTestData.class).count();
    double threshold = (double) configuration.getMutationTestThreshold() / 100.0;
    return new PositiveEvaluation(available, denominator, numerator, threshold);
  }
}
