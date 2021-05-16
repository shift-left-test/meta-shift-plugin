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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the DigestUtils class.
 *
 * @author Sung Gon Kim
 */
public class DigestUtilsTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
  }

  @Test(expected = IOException.class)
  public void testSha1WithUnknownPath() throws IOException {
    DigestUtils.sha1(utils.getPath("path-to-unknown"));
  }

  @Test
  public void testSha1WithUnknownPathAndDefaultValue() {
    assertEquals("X", DigestUtils.sha1(utils.getPath("path-to-unknown"), "X"));
  }

  @Test(expected = IOException.class)
  public void testSha1WithDirectory() throws IOException {
    DigestUtils.sha1(utils.createDirectory("directory"));
  }

  @Test
  public void testSha1WithDirectoryAndDefaultValue() throws IOException {
    assertEquals("X", DigestUtils.sha1(utils.createDirectory("directory"), "X"));
  }

  @Test
  public void testEmptyFile() throws IOException {
    File directory = utils.createDirectory("directory");
    File file = utils.writeLines(new StringBuilder(), directory, "a.file");
    assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", DigestUtils.sha1(file));
  }

  @Test
  public void testSha1OfEmptyString() {
    assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", DigestUtils.sha1(""));
  }

  @Test
  public void testSha1OfEmptyStringWithDefaultValue() {
    assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", DigestUtils.sha1("", "X"));
  }
}
