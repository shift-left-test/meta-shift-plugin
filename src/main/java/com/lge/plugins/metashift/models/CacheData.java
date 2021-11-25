/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the cache data.
 *
 * @author Sung Gon Kim
 */
public abstract class CacheData extends Data {

  /**
   * Represents the cache data type.
   */
  protected enum Type {
    PREMIRROR,
    SHARED_STATE,
  }

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -8870145998645249730L;

  /**
   * Represents the cache signature.
   */
  private final String signature;

  /**
   * Indicates the cache availability.
   */
  private final boolean available;

  /**
   * Represents the type of the cache.
   */
  private final Type type;

  /**
   * Default constructor.
   *
   * @param recipe    name
   * @param signature name
   * @param available the cache availability
   * @param type      of the cache
   */
  public CacheData(String recipe, String signature, boolean available, Type type) {
    super(recipe);
    this.signature = signature;
    this.available = available;
    this.type = type;
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    CacheData other = (CacheData) object;
    return new EqualsBuilder()
        .append(getSignature(), other.getSignature())
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getSignature())
        .toHashCode();
  }

  /**
   * Return the cache signature.
   *
   * @return cache signature
   */
  public final String getSignature() {
    return signature;
  }

  /**
   * Return the cache availability.
   *
   * @return true if the cache is available, false otherwise.
   */
  public final boolean isAvailable() {
    return available;
  }

  /**
   * Returns the type of the cache.
   *
   * @return cache type
   */
  public final String getType() {
    return type.name();
  }
}
