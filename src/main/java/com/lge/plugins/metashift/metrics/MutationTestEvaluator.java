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
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.Streamable;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * MutationTestEvaluator class.
 *
 * @author Sung Gon Kim
 */
public final class MutationTestEvaluator extends PositiveEvaluator<MutationTestEvaluator> {

  /**
   * Represents the mutation test types.
   */
  private enum Type {
    /**
     * Killed mutation test.
     */
    KILLED,

    /**
     * Survived mutation test.
     */
    SURVIVED,

    /**
     * Skipped mutation test.
     */
    SKIPPED,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, Counter> collection;

  /**
   * Represents the build status of the metric.
   */
  private final boolean buildStatus;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public MutationTestEvaluator(final Configuration configuration) {
    super((double) configuration.getMutationTestThreshold() / 100.0);
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, new Counter()));
    buildStatus = configuration.isMutationTestAsUnstable();
  }

  /**
   * Returns the killed mutation test counter.
   *
   * @return a counter object
   */
  public Counter getKilled() {
    return collection.get(Type.KILLED);
  }

  /**
   * Returns the survived mutation test counter.
   *
   * @return a counter object
   */
  public Counter getSurvived() {
    return collection.get(Type.SURVIVED);
  }

  /**
   * Returns the skipped mutation test counter.
   *
   * @return a counter object
   */
  public Counter getSkipped() {
    return collection.get(Type.SKIPPED);
  }

  @Override
  public boolean isStable() {
    return !buildStatus || !isAvailable() || isQualified();
  }

  @Override
  protected void parseImpl(final Streamable c) {
    collection.put(Type.KILLED, new Counter(
        c.objects(MutationTestData.class).count(),
        c.objects(KilledMutationTestData.class).count()
    ));
    collection.put(Type.SURVIVED, new Counter(
        c.objects(MutationTestData.class).count(),
        c.objects(SurvivedMutationTestData.class).count()
    ));
    collection.put(Type.SKIPPED, new Counter(
        c.objects(MutationTestData.class).count(),
        c.objects(SkippedMutationTestData.class).count()
    ));

    setAvailable(c.isAvailable(MutationTestData.class));
    setDenominator(getKilled().getDenominator());
    setNumerator(getKilled().getNumerator());
  }
}
