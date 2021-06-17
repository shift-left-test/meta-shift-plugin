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

import com.lge.plugins.metashift.models.Criteria;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.TestData;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * TestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class TestEvaluator extends PositiveEvaluator<TestEvaluator> {

  /**
   * Represents the test types.
   */
  private enum Type {
    /**
     * Passed tests.
     */
    PASSED,

    /**
     * Failed tests.
     */
    FAILED,

    /**
     * Error tests.
     */
    ERROR,

    /**
     * Skipped tests.
     */
    SKIPPED,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, Counter> collection;

  /**
   * Default constructor.
   *
   * @param criteria for evaluation
   */
  public TestEvaluator(final Criteria criteria) {
    super((double) criteria.getTestThreshold() / 100.0);
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
  }

  /**
   * Returns the passed test counter.
   *
   * @return counter object
   */
  public Counter getPassed() {
    return collection.get(Type.PASSED);
  }

  /**
   * Returns the failed test counter.
   *
   * @return counter object
   */
  public Counter getFailed() {
    return collection.get(Type.FAILED);
  }

  /**
   * Returns the error test counter.
   *
   * @return counter object
   */
  public Counter getError() {
    return collection.get(Type.ERROR);
  }

  /**
   * Returns the skipped test counter.
   *
   * @return counter object
   */
  public Counter getSkipped() {
    return collection.get(Type.SKIPPED);
  }

  @Override
  protected void parseImpl(final Streamable c) {
    collection.put(Type.PASSED, new Counter(
        c.objects(TestData.class).count(),
        c.objects(PassedTestData.class).count()
    ));
    collection.put(Type.FAILED, new Counter(
        c.objects(TestData.class).count(),
        c.objects(FailedTestData.class).count()
    ));
    collection.put(Type.ERROR, new Counter(
        c.objects(TestData.class).count(),
        c.objects(ErrorTestData.class).count()
    ));
    collection.put(Type.SKIPPED, new Counter(
        c.objects(TestData.class).count(),
        c.objects(SkippedTestData.class).count()
    ));

    setAvailable(c.isAvailable(TestData.class));
    setDenominator(getPassed().getDenominator());
    setNumerator(getPassed().getNumerator());
  }
}
