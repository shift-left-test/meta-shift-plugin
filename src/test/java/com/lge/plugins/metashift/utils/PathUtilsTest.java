/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
  public void testEmptyFiles() {
    assertFalse(PathUtils.isHidden(""));
    assertFalse(PathUtils.isHidden(" "));
  }

  @Test
  public void testNormalFiles() {
    assertFalse(PathUtils.isHidden("abc"));
    assertFalse(PathUtils.isHidden("abc.file"));
  }

  @Test
  public void testHiddenFiles() {
    assertTrue(PathUtils.isHidden(".abc"));
    assertTrue(PathUtils.isHidden(".abc.file"));
  }

  @Test
  public void testNormalPaths() {
    assertFalse(PathUtils.isHidden("path/to/abc.file"));
  }

  @Test
  public void testHiddenPaths() {
    assertTrue(PathUtils.isHidden(".path/to/abc.file"));
    assertTrue(PathUtils.isHidden("path/.to/abc.file"));
    assertTrue(PathUtils.isHidden("path/to/.abc.file"));
  }

  @Test
  public void testRelativePaths() {
    assertFalse(PathUtils.isHidden("../path/to/a.file"));
    assertFalse(PathUtils.isHidden("path/to/../a.file"));
  }

  @Test
  public void testDotPaths() {
    assertFalse(PathUtils.isHidden("./path/to/a.file"));
    assertFalse(PathUtils.isHidden("path/to/./a.file"));
  }

  @Test
  public void testDoubleDotHiddenPaths() {
    assertTrue(PathUtils.isHidden("..path/to/a.file"));
    assertTrue(PathUtils.isHidden("path/..to/a.file"));
    assertTrue(PathUtils.isHidden("path/to/..a.file"));
  }
}