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

/**
 * RecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class RecipeReport extends Report<RecipeGroup> {

  private static final long serialVersionUID = 6265637139439193655L;

  private final DataSource dataSource;
  private final String recipe;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   * @param recipe     name
   */
  public RecipeReport(DataSource dataSource, String recipe) {
    this.dataSource = dataSource;
    this.recipe = recipe;
    Stream.of(Metric.active()).forEach(o -> put(o, new RecipeGroup(dataSource, o, recipe)));
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

  /**
   * Returns the stored source of the file. The source is recipe-scoped, not
   * metric-scoped.
   *
   * @param file name
   * @return source text, empty when not stored
   */
  public String readFile(String file) {
    // Null on a report that only partly deserialized, despite being final (see Report#get).
    if (dataSource == null || recipe == null) {
      return "";
    }
    String data = dataSource.get(
        Scope.RECIPE.name(), Metric.NONE.name(), Data.FILE.name(), recipe, file);
    return Optional.ofNullable(data).orElse("");
  }

  /**
   * Tests if the source of the file is stored.
   *
   * @param file name
   * @return true when the source is stored
   */
  public boolean hasFile(String file) {
    if (dataSource == null || recipe == null) {
      return false;
    }
    return dataSource.has(Scope.RECIPE.name(), Metric.NONE.name(), Data.FILE.name(), recipe, file);
  }
}
