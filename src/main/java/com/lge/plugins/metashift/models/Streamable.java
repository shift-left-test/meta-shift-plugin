/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.stream.Stream;

/**
 * Streamable interface.
 *
 * @author Sung Gon Kim
 */
public interface Streamable {

  /**
   * Test if the given class type objects are available.
   *
   * @param clazz class type
   * @return true if the object type is available, false otherwise
   */
  <T> boolean contains(final Class<T> clazz);

  /**
   * Returns the stream of the given class type.
   *
   * @param clazz class type
   * @param <T>   type of the stream
   * @return a stream object
   */
  <T> Stream<T> objects(final Class<T> clazz);
}
