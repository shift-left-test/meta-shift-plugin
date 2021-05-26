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

/**
 * A queryable interface for metrics.
 *
 * @author Sung Gon Kim
 */
public interface Queryable<T> {

  /**
   * Returns the cache availability object.
   *
   * @return cache availability object
   */
  @Deprecated
  T getCacheAvailability();

  /**
   * Returns the premirror cache object.
   *
   * @return an evaluator object
   */
  T getPremirrorCache();

  /**
   * Returns the shared state cache object.
   *
   * @return an evaluator object
   */
  T getSharedStateCache();

  /**
   * Returns the code violation object.
   *
   * @return code violation object
   */
  T getCodeViolations();

  /**
   * Returns the comment object.
   *
   * @return comment object
   */
  T getComments();

  /**
   * Returns the complexity object.
   *
   * @return complexity object
   */
  T getComplexity();

  /**
   * Returns the coverage object.
   *
   * @return coverage object
   */
  T getCoverage();

  /**
   * Returns the duplication object.
   *
   * @return duplication object
   */
  T getDuplications();

  /**
   * Returns the mutation test object.
   *
   * @return mutation test object
   */
  T getMutationTest();

  /**
   * Returns the recipe violation object.
   *
   * @return recipe violation object
   */
  T getRecipeViolations();

  /**
   * Returns the test object.
   *
   * @return test object
   */
  T getTest();
}
