/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the PathUtils class.
 *
 * @author Sung Gon Kim
 */
public class PathUtilsTest {

  @Test
  public void testAllowsPlainRelativePaths() {
    assertFalse(PathUtils.escapesRoot("a.cpp"));
    assertFalse(PathUtils.escapesRoot("dir/a.cpp"));
    assertFalse(PathUtils.escapesRoot("dir/sub/a.cpp"));
    assertFalse(PathUtils.escapesRoot(""));
  }

  @Test
  public void testRejectsParentTraversal() {
    assertTrue(PathUtils.escapesRoot(".."));
    assertTrue(PathUtils.escapesRoot("../a.cpp"));
    assertTrue(PathUtils.escapesRoot("dir/../../a.cpp"));
    assertTrue(PathUtils.escapesRoot("dir/..\\a.cpp"));
  }

  @Test
  public void testRejectsAbsolutePaths() {
    assertTrue(PathUtils.escapesRoot("/etc/passwd"));
    assertTrue(PathUtils.escapesRoot("\\Windows\\System32"));
    assertTrue(PathUtils.escapesRoot("C:\\Windows\\System32"));
  }

  @Test
  public void testAllowsDotsWithinASegmentName() {
    // "a..b" is not the ".." segment, only a filename containing dots
    assertFalse(PathUtils.escapesRoot("a..b.cpp"));
  }
}
