/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;

/**
 * FakeReport interface.
 *
 * @author Sung Gon Kim
 */
public interface FakeReport {

  /**
   * Creates report files under the given directory.
   *
   * @param directory to create files
   */
  void toFile(final File directory) throws IOException;
}
