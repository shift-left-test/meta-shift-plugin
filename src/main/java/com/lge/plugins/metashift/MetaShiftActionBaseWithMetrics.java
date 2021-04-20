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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Streamable;
import hudson.model.Actionable;
import java.text.DecimalFormat;

/**
 * Base Action class which has metrics and api.
 */
public abstract class MetaShiftActionBaseWithMetrics extends Actionable {

  private final Metrics metrics;

  public MetaShiftActionBaseWithMetrics(Criteria criteria, Streamable streamable) {
    this.metrics = new Metrics(criteria);
    this.metrics.parse(streamable);
  }

  public Metrics getMetrics() {
    return this.metrics;
  }

  ///helper for ui
  public String getFixedFloat(String format, float number) {
    return new DecimalFormat(format).format(number);
  }
}
