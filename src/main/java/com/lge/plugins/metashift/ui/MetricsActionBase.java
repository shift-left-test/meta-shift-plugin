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

package com.lge.plugins.metashift.ui;

import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.Streamable;
import hudson.model.Actionable;
import net.sf.json.JSONObject;

/**
 * Actionable class with metrics.
 */
public abstract class MetricsActionBase extends Actionable {

  private final Metrics metrics;

  /**
   * constructor.
   *
   * @param criteria   criteria
   * @param streamable metrics parsing applies to
   */
  public MetricsActionBase(Criteria criteria, Streamable streamable) {
    super();

    this.metrics = new Metrics(criteria);
    this.metrics.parse(streamable);
  }

  public Metrics getMetrics() {
    return this.metrics;
  }

  public JSONObject getCodeSizeJson() {
    return metrics.getCodeSize().toJsonObject();
  }

  public JSONObject getPremirrorCacheJson() {
    return metrics.getPremirrorCache().toJsonObject();
  }

  public JSONObject getSharedStateCacheJson() {
    return metrics.getSharedStateCache().toJsonObject();
  }

  public JSONObject getCodeViolationsJson() {
    return metrics.getCodeViolations().toJsonObject();
  }

  public JSONObject getCommentsJson() {
    return metrics.getComments().toJsonObject();
  }

  public JSONObject getComplexityJson() {
    return metrics.getComplexity().toJsonObject();
  }

  public JSONObject getStatementCoverageJson() {
    return metrics.getStatementCoverage().toJsonObject();
  }

  public JSONObject getBranchCoverageJson() {
    return metrics.getBranchCoverage().toJsonObject();
  }

  public JSONObject getDuplicationsJson() {
    return metrics.getDuplications().toJsonObject();
  }

  public JSONObject getMutationTestJson() {
    return metrics.getMutationTest().toJsonObject();
  }

  public JSONObject getRecipeViolationsJson() {
    return metrics.getRecipeViolations().toJsonObject();
  }

  public JSONObject getTestJson() {
    return metrics.getTest().toJsonObject();
  }
}
