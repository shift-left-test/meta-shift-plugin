/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRUCache implementation class.
 *
 * @author Sung Gon Kim
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 3817409099024705244L;

  /**
   * Represents the default capacity.
   */
  private static final int DEFAULT_CAPACITY = 10;

  /**
   * Represents the capacity of the cache.
   */
  private final int capacity;

  /**
   * Default constructor.
   */
  public LruCache() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Default constructor.
   *
   * @param capacity of the initial cache
   */
  public LruCache(final int capacity) {
    super(capacity, 0.75f, true);
    this.capacity = capacity;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity;
  }
}
