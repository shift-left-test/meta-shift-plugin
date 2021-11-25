/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.io.Serializable;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Represents a data object.
 *
 * @author Sung Gon Kim
 */
public abstract class Data implements Serializable, Comparable<Data> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7672797936775946949L;

  /**
   * Represents the name of the data.
   */
  private final String name;

  /**
   * Default constructor.
   *
   * @param name of the data
   */
  public Data(final String name) {
    this.name = name;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return true if this is the same as the other, false otherwise
   */
  @Override
  public abstract boolean equals(Object object);

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public abstract int hashCode();

  @Override
  public int compareTo(Data other) {
    return new CompareToBuilder()
        .append(getName(), other.getName())
        .toComparison();
  }

  /**
   * Returns the name of the data.
   *
   * @return the name of the data
   */
  public String getName() {
    return name;
  }
}
