/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.stream.Stream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a recipe containing various data objects for metrics.
 *
 * @author Sung Gon Kim
 */
public final class Recipe extends Data implements Streamable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -3212169684403928336L;

  /**
   * Represents the data list.
   */
  private final DataList dataList;

  /**
   * Represents the source directory of the recipe (the BitBake S variable),
   * null when metadata.json is not available.
   */
  private String sourceDir;

  /**
   * Creates an empty Recipe object with the given recipe name.
   *
   * @param name of the recipe
   */
  public Recipe(final String name) throws IllegalArgumentException {
    this(name, new DataList());
  }

  /**
   * Create a Recipe object with the given recipe name.
   *
   * @param name     of the recipe
   * @param dataList objects
   */
  public Recipe(final String name, final DataList dataList) {
    super(name);
    this.dataList = dataList;
  }

  /**
   * Adds the given object to the list.
   *
   * @param object to add
   * @param <T>    object type
   */
  public <T> void add(final T object) {
    dataList.add(object);
  }

  /**
   * Returns the source directory of the recipe.
   *
   * @return absolute source directory path, or null when unknown
   */
  public String getSourceDir() {
    return sourceDir;
  }

  /**
   * Sets the source directory of the recipe.
   *
   * @param sourceDir absolute source directory path
   */
  public void setSourceDir(final String sourceDir) {
    this.sourceDir = sourceDir;
  }

  @Override
  public <T> boolean contains(final Class<T> clazz) {
    return dataList.contains(clazz);
  }

  @Override
  public <T> Stream<T> objects(final Class<T> clazz) {
    return dataList.objects(clazz);
  }

  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    Recipe other = (Recipe) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .toHashCode();
  }
}
