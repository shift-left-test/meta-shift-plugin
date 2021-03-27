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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FileFinder class.
 *
 * @author Sung Gon Kim
 */
public class FileFinderTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FileFinder finder;

  @Before
  public void setUp() throws Exception {
    File directory = folder.newFolder("path/to");
    FileUtils.touch(new File(directory, "report1.xml"));
    FileUtils.touch(new File(directory, "report2.xml"));
  }

  @Test
  public void testFindWithEmptyPattern() {
    finder = new FileFinder(StringUtils.EMPTY);
    assertEquals(0, finder.find(folder.getRoot()).length);
  }

  @Test
  public void testFindWithoutMatchingPattern() {
    finder = new FileFinder("**/report*.json");
    assertEquals(0, finder.find(folder.getRoot()).length);
  }

  @Test
  public void testFindWithMatchingPattern() {
    finder = new FileFinder("**/report*.xml");
    assertEquals(2, finder.find(folder.getRoot()).length);
  }

  @Test
  public void testFindWithExcludePattern() {
    finder = new FileFinder("**/report*.xml", "**/to/**");
    assertEquals(0, finder.find(folder.getRoot()).length);
  }
}
