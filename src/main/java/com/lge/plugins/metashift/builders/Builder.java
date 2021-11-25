/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import java.io.IOException;

/**
 * Builder interface.
 *
 * @author Sung Gon Kim
 */
public interface Builder<T, R> {

  /**
   * Parses the object to create the result.
   *
   * @param object to parse
   * @return result
   * @throws IOException          if failed to operate with files
   * @throws InterruptedException if an interruption occurs
   */
  R parse(T object) throws IOException, InterruptedException;
}
