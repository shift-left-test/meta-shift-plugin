/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the premirror cache data.
 *
 * @author Sung Gon Kim
 */
public final class PremirrorCacheData extends CacheData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 1904932992371956239L;

  /**
   * Default constructor.
   *
   * @param recipe    name
   * @param signature name
   * @param available the cache availability
   */
  public PremirrorCacheData(final String recipe, final String signature, final boolean available) {
    super(recipe, signature, available, Type.PREMIRROR);
  }
}
