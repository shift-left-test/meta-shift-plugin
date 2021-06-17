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

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * ViolationEvaluator class.
 *
 * @author Sung Gon Kim
 */
public abstract class ViolationEvaluator<T> extends NegativeEvaluator<T> {

  /**
   * Represents the code violation types.
   */
  private enum Type {
    /**
     * Major violations.
     */
    MAJOR,

    /**
     * Minor violations.
     */
    MINOR,

    /**
     * Info violations.
     */
    INFO,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, Counter> collection;

  /**
   * Default constructor.
   *
   * @param threshold for evaluation
   */
  public ViolationEvaluator(final double threshold) {
    super(threshold);
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
  }

  /**
   * Returns the major code violation counter object.
   *
   * @return counter object
   */
  public Counter getMajor() {
    return collection.get(Type.MAJOR);
  }

  /**
   * Sets the major code violation counter object.
   *
   * @param counter object
   */
  protected void setMajor(Counter counter) {
    collection.put(Type.MAJOR, counter);
  }

  /**
   * Returns the minor code violation counter object.
   *
   * @return counter object
   */
  public Counter getMinor() {
    return collection.get(Type.MINOR);
  }

  /**
   * Sets the minor code violation counter object.
   *
   * @param counter object
   */
  protected void setMinor(Counter counter) {
    collection.put(Type.MINOR, counter);
  }

  /**
   * Returns the info code violation counter object.
   *
   * @return counter object
   */
  public Counter getInfo() {
    return collection.get(Type.INFO);
  }

  /**
   * Sets the info code violation counter object.
   *
   * @param counter object
   */
  protected void setInfo(Counter counter) {
    collection.put(Type.INFO, counter);
  }
}
