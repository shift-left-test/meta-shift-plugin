/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import com.lge.plugins.metashift.builders.Constants.Data;
import com.lge.plugins.metashift.builders.Constants.Metric;
import com.lge.plugins.metashift.builders.Constants.Scope;
import com.lge.plugins.metashift.persistence.DataSource;
import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ProjectReport class.
 *
 * @author Sung Gon Kim
 */
public class ProjectReport extends Report<ProjectGroup> {

  private static final long serialVersionUID = 7057813789045080156L;

  private final DataSource dataSource;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   */
  public ProjectReport(DataSource dataSource) {
    this.dataSource = dataSource;
    Stream.of(Metric.active()).forEach(o -> put(o, new ProjectGroup(dataSource, o)));
  }

  /**
   * Points this report's store at the given storage directory, which is where its files are now.
   *
   * @param storage directory holding the stored objects and their index
   */
  public void relocate(File storage) {
    if (dataSource != null) {
      dataSource.relocate(storage);
    }
  }

  private <T> T getOrDefault(Metric metric, Data data, T defaultValue) {
    // dataSource is serialized after groups, so a report that only partly deserialized has it
    // null even though it is declared final (see Report#get).
    if (dataSource == null) {
      return defaultValue;
    }
    T o = dataSource.get(Scope.PROJECT.name(), metric.name(), data.name());
    return Optional.ofNullable(o).orElse(defaultValue);
  }

  /**
   * Returns the summary objects.
   *
   * @return summary objects
   */
  public JSONArray getSummaries() {
    return getOrDefault(Metric.NONE, Data.SUMMARIES, new JSONArray());
  }

  /**
   * Returns the failed and errored unit tests of the whole project, capped at the row limit
   * applied when publishing.
   *
   * @return failed/error TestData rows, empty when this build predates them being precomputed
   */
  public JSONArray getFailedTests() {
    return getOrDefault(Metric.UNIT_TESTS, Data.FAILED_OBJECTS, new JSONArray());
  }

  /**
   * Returns the survived mutations of the whole project, capped at the row limit applied when
   * publishing.
   *
   * @return survived MutationTestData rows, empty when this build predates them being precomputed
   */
  public JSONArray getSurvivedMutations() {
    return getOrDefault(Metric.MUTATION_TESTS, Data.SURVIVED_OBJECTS, new JSONArray());
  }

  /**
   * Returns the untruncated totals behind {@link #getFailedTests()} and
   * {@link #getSurvivedMutations()}.
   *
   * @return counts object, empty when this build predates them being precomputed
   */
  public JSONObject getCounts() {
    return getOrDefault(Metric.NONE, Data.COUNTS, new JSONObject());
  }
}
