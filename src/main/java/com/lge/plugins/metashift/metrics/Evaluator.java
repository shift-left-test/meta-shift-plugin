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

import com.lge.plugins.metashift.models.BuildStatus;
import com.lge.plugins.metashift.models.Streamable;
import net.sf.json.JSONObject;

/**
 * Evaluator class.
 *
 * @param <T> class type
 * @author Sung Gon Kim
 */
public abstract class Evaluator<T> extends Counter {

  /**
   * Represents the threshold.
   */
  private final double threshold;

  /**
   * Represents the evaluator availability.
   */
  private boolean available;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public Evaluator(final double threshold) {
    this.threshold = threshold;
    this.available = false;
  }

  /**
   * Returns the threshold.
   *
   * @return threshold value
   */
  public final double getThreshold() {
    return threshold;
  }

  /**
   * Returns whether the metric is available.
   *
   * @return true if the metric is available, false otherwise
   */
  public boolean isAvailable() {
    return available;
  }

  /**
   * Set the evaluator availability.
   *
   * @param available evaluator availability
   */
  protected void setAvailable(final boolean available) {
    this.available = available;
  }

  /**
   * Returns whether the evaluation of the metric meets the criteria.
   *
   * @return true if the metric meets the criteria, false otherwise
   */
  public abstract boolean isQualified();

  /**
   * Returns the build status based on the evaluation of the metrics.
   *
   * @param status for evaluation
   * @return true if the build status is stable, false otherwise
   */
  public abstract boolean isStable(final BuildStatus status);

  /**
   * Parse the given object to create metric data.
   *
   * @param object to parse
   * @return self
   */
  @SuppressWarnings("unchecked")
  public final T parse(final Streamable object) {
    setAvailable(false);
    setDenominator(0);
    setNumerator(0);
    parseImpl(object);
    return (T) this;
  }

  /**
   * Evaluate the given object.
   *
   * @param c object to evaluate
   */
  protected abstract void parseImpl(final Streamable c);

  /**
   * Returns the object as a JSONObject instance.
   *
   * @return a JSONObject instance
   */
  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}
