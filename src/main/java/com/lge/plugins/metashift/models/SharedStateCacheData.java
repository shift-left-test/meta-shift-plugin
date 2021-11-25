/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the shared state cache data.
 *
 * @author Sung Gon Kim
 */
public final class SharedStateCacheData extends CacheData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 3499107394956644333L;

  /**
   * Default constructor.
   *
   * @param recipe    name
   * @param signature name
   * @param available the cache availability
   */
  public SharedStateCacheData(final String recipe, final String signature,
      final boolean available) {
    super(recipe, signature, available, Type.SHARED_STATE);
  }
}
