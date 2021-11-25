/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.IOException;

/**
 * FakeFile interface.
 *
 * @author Sung Gon Kim
 */
public interface FakeFile {

  /**
   * Create the source files under the source path of the recipe.
   */
  void toFile() throws IOException;
}
