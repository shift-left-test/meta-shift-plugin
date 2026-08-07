/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

/**
 * Path safety helpers shared by every feature that resolves a report-relative path supplied by
 * report data or an HTTP request against a fixed root directory.
 *
 * @author Sung Gon Kim
 */
public final class PathUtils {

  private PathUtils() {
  }

  /**
   * Tests whether the given root-relative path could escape its root, OS-independently: an
   * absolute path (POSIX or Windows-drive-letter) or any {@code ..} segment.
   *
   * @param path root-relative path to test
   * @return true when the path must not be resolved against the root
   */
  public static boolean escapesRoot(final String path) {
    if (path.startsWith("/") || path.startsWith("\\") || path.matches("^[A-Za-z]:.*")) {
      return true;
    }
    for (String segment : path.split("[/\\\\]")) {
      if (segment.equals("..")) {
        return true;
      }
    }
    return false;
  }
}
