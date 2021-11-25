/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

/**
 * Collector interface.
 *
 * @param <T> input type
 * @param <R> output type
 * @author Sung Gon Kim
 */
public interface Collector<T, R> {

  /**
   * Parses the given object to return the result.
   *
   * @param object to parse
   * @return parsed result
   */
  R parse(T object);
}
