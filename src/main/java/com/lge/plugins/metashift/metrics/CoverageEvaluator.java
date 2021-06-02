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
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.Streamable;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * CoverageEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class CoverageEvaluator extends PositiveEvaluator<CoverageEvaluator> {

  /**
   * Represents the coverage types.
   */
  private enum Type {
    /**
     * Statement coverage.
     */
    STATEMENT,

    /**
     * Branch coverage.
     */
    BRANCH,
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
  public CoverageEvaluator(final Criteria criteria) {
    super((double) criteria.getCoverageThreshold() / 100.0);
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
  }

  /**
   * Returns the statement coverage counter.
   *
   * @return counter object
   */
  public Counter getStatement() {
    return collection.get(Type.STATEMENT);
  }

  /**
   * Returns the branch coverage counter.
   *
   * @return counter object
   */
  public Counter getBranch() {
    return collection.get(Type.BRANCH);
  }

  @Override
  protected void parseImpl(final Streamable c) {
    collection.put(Type.STATEMENT, new Counter(
        c.objects(StatementCoverageData.class).count(),
        c.objects(StatementCoverageData.class).filter(CoverageData::isCovered).count()
    ));
    collection.put(Type.BRANCH, new Counter(
        c.objects(BranchCoverageData.class).count(),
        c.objects(BranchCoverageData.class).filter(CoverageData::isCovered).count()
    ));

    setAvailable(c.isAvailable(CoverageData.class));
    setDenominator(collection.values().stream().mapToLong(Counter::getDenominator).sum());
    setNumerator(collection.values().stream().mapToLong(Counter::getNumerator).sum());
  }
}
